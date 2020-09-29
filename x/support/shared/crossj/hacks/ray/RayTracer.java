package crossj.hacks.ray;

import crossj.Func3;
import crossj.IO;
import crossj.M;
import crossj.Num;
import crossj.Rand;
import crossj.Time;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Surface;

public final class RayTracer {
    public static final int DEFAULT_SAMPLES_PER_PIXEL = 20;

    private final Camera camera;
    private final int imageWidth;
    private final boolean gammaCorrection;
    private final int samplesPerPixel;
    private final int maxDepth;
    private final boolean verbose;

    public RayTracer(Camera camera, int imageWidth, boolean gammaCorrection, int samplesPerPixel, int maxDepth,
            boolean verbose) {
        this.camera = camera;
        this.imageWidth = imageWidth;
        this.gammaCorrection = gammaCorrection;
        this.samplesPerPixel = samplesPerPixel;
        this.maxDepth = maxDepth;
        this.verbose = verbose;
    }

    public static RayTracer getDefault() {
        return new RayTracer(Camera.getDefault(), 800, true, DEFAULT_SAMPLES_PER_PIXEL, 200, false);
    }

    public RayTracer withCamera(Camera camera) {
        return new RayTracer(camera, imageWidth, gammaCorrection, samplesPerPixel, maxDepth, verbose);
    }

    public RayTracer withImageWidth(int imageWidth) {
        return new RayTracer(camera, imageWidth, gammaCorrection, samplesPerPixel, maxDepth, verbose);
    }

    public RayTracer withGammaCorrection(boolean gammaCorrection) {
        return new RayTracer(camera, imageWidth, gammaCorrection, samplesPerPixel, maxDepth, verbose);
    }

    public RayTracer withSamplesPerPixel(int samplesPerPixel) {
        return new RayTracer(camera, imageWidth, gammaCorrection, samplesPerPixel, maxDepth, verbose);
    }

    public RayTracer withMaxDepth(int maxDepth) {
        return new RayTracer(camera, imageWidth, gammaCorrection, samplesPerPixel, maxDepth, verbose);
    }

    public RayTracer withVerbose(boolean verbose) {
        return new RayTracer(camera, imageWidth, gammaCorrection, samplesPerPixel, maxDepth, verbose);
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return (int) (imageWidth / camera.getAspectRatio());
    }

    public void renderToBitmapFile(Surface scene, String filepath) {
        var bitmap = renderToBitmap(scene);
        IO.writeFileBytes(filepath, bitmap.toBMPBytes());
    }

    public Bitmap renderToBitmap(Surface scene) {
        var imageHeight = getImageHeight();
        var bitmap = Bitmap.withDimensions(imageWidth, imageHeight);
        render(scene, (x, y, color) -> {
            // The coordinate system of the Bitmap class is flipped (i.e. origin is
            // upper-left corner) compared to the RayTracer's coordinate system (i.e. origin
            // is lower-left corner).
            bitmap.setColor(x, imageHeight - 1 - y, color);
            return null;
        });
        return bitmap;
    }

    public void render(Surface scene, Func3<Void, Integer, Integer, Color> pixelCallback) {
        // we go top down to match more or less our expectation of which pixels should
        // be
        // rendered first.
        var rng = Rand.getDefault();
        var startTime = Time.now();
        var lastUpdate = -1.0;
        var imageHeight = getImageHeight();
        var imageWidth = getImageWidth();
        for (int y = imageHeight - 1; y >= 0; y--) {
            if (verbose) {
                var doneRatio = (imageHeight - y) / (double) imageHeight;
                if (doneRatio - lastUpdate >= 0.05) {
                    var elapsedTime = Num.format(Time.now() - startTime);
                    IO.println(((int) (doneRatio * 100)) + "% done (" + elapsedTime + "s elapsed)");
                    lastUpdate = doneRatio;
                }
            }
            for (int x = 0; x < imageWidth; x++) {
                var red = 0.0;
                var green = 0.0;
                var blue = 0.0;
                for (int i = 0; i < samplesPerPixel; i++) {
                    var u = (x + rng.next()) / (imageWidth - 1);
                    var v = (y + rng.next()) / (imageHeight - 1);
                    var ray = camera.getRay(u, v);
                    var color = rayColor(ray, scene, maxDepth, verbose);
                    red += color.r;
                    green += color.g;
                    blue += color.b;
                }
                red /= samplesPerPixel;
                green /= samplesPerPixel;
                blue /= samplesPerPixel;

                if (gammaCorrection) {
                    red = M.sqrt(red);
                    green = M.sqrt(green);
                    blue = M.sqrt(blue);
                }

                pixelCallback.apply(x, y, Color.rgb(red, green, blue));
            }
        }
        var endTime = Time.now();
        if (verbose) {
            IO.println("Render finished in " + (endTime - startTime) + " seconds");
        }
    }

    private static Color rayColor(Ray ray, Surface world, int depth, boolean verbose) {
        var startRay = ray;

        var tryHit = world.hitRay(ray);

        var totalAttenuation = Color.WHITE;

        while (tryHit.isPresent() && depth > 0 && totalAttenuation.rgb2Norm2() > 0.00001) {
            var hit = tryHit.get();
            var pair = hit.scatter();
            var colorAttenuation = pair.get1();
            ray = pair.get2();
            totalAttenuation = totalAttenuation.multiply(colorAttenuation);
            depth--;
            tryHit = world.hitRay(ray);
        }

        if (depth <= 0) {
            // If we get here, it means, that the rendering will actually be a little less
            // accurate and the image a bit darker, because we couldn't find any lightsource
            // in the given number of ray bounces, but at the same time, we couldn't prove
            // that the cumulative attenuation was enough to ignore this ray.
            if (verbose) {
                // Here, we rerun a simulation of the ray bounces, and print out our
                // intermediate rays to help with debugging.
                IO.println("total miss (" + startRay + ")");
                var r = startRay;
                for (int i = 0; i < 10; i++) {
                    var th = world.hitRay(r);
                    if (th.isEmpty()) {
                        IO.println("LIGHT SOURCE FOUND");
                        break;
                    } else {
                        r = th.get().scatter().get2();
                        IO.println("    " + r);
                    }
                }
            }
            return Color.BLACK;
        }

        // ray is going off into the world
        // assume a blue-ish sky
        var unitDirection = ray.getDirection().normalize();
        var t = 0.5 * (unitDirection.getY() + 1);
        return totalAttenuation.multiply(Color.WHITE.scale(1 - t).add(Color.rgb(0.5, 0.7, 1.0).scale(t)));
    }
}
