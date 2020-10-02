package crossj.hacks.ray.main.x05;

import crossj.IO;
import crossj.M;
import crossj.Rand;
import crossj.Time;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Sphere;
import crossj.hacks.ray.geo.Surface;
import crossj.hacks.ray.geo.Surfaces;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Metal;
import crossj.hacks.ray.math.Matrix;

/**
 * Metals
 */
public final class Main {
    public static void main(String[] args) {
        var samplesPerPixel = 20;
        var aspectRatio = Camera.DEFAULT_ASPECT_RATIO;
        var imageWidth = 400;
        var imageHeight = (int) (imageWidth / aspectRatio);
        var canvas = Bitmap.withDimensions(imageWidth, imageHeight);
        var maxDepth = 200;
        var rng = Rand.getDefault();

        var camera = Camera.getDefault();
        var world = Surfaces.of(
                Sphere.withMaterial(Lambertian.withColor(Color.rgb(0.2, 0.2, 0.4)))
                        .andTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(0, 0, -1.5)),
                Sphere.withMaterial(Metal.withColor(Color.rgb(0.5, 0.5, 0.5)))
                    .andTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(-1.5, 0, -1)),
                    Sphere.withMaterial(Metal.withColor(Color.rgb(0.5, 0.2, 0.2)).andFuzz(0.3))
                        .andTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(1.5, 0, -1.2)),
                Sphere.withTransform(Matrix.scaling(100, 100, 100).thenTranslate(0, -100.5, -1)));

        var lastUpdate = -1.0;

        IO.println("Starting render");
        var start = Time.now();
        for (int y = imageHeight - 1; y >= 0; y--) {
            var doneRatio = (imageHeight - y) / (double) imageHeight;
            if (doneRatio - lastUpdate >= 0.05) {
                IO.println(((int) (doneRatio * 100)) + "% done");
                lastUpdate = doneRatio;
            }

            for (int x = 0; x < imageWidth; x++) {
                var red = 0.0;
                var green = 0.0;
                var blue = 0.0;
                for (int i = 0; i < samplesPerPixel; i++) {
                    var u = (x + rng.next()) / (imageWidth - 1);
                    var v = (y + rng.next()) / (imageHeight - 1);
                    var ray = camera.getRay(u, v);
                    var color = rayColor(ray, world, maxDepth);
                    red += color.r;
                    green += color.g;
                    blue += color.b;
                }
                red /= samplesPerPixel;
                green /= samplesPerPixel;
                blue /= samplesPerPixel;

                // account for gamma correction
                red = M.sqrt(red);
                green = M.sqrt(green);
                blue = M.sqrt(blue);

                // Bitmap's (0, 0) is the upper-left corner, so the y coordinates
                // have to be flipped
                canvas.setColor(x, imageHeight - 1 - y, Color.rgb(red, green, blue));
            }
        }
        var end = Time.now();
        IO.println("Render finished in " + (end - start) + " seconds");

        IO.writeFileBytes("out/ray3/x05.bmp", canvas.toBMPBytes());
    }

    public static Color rayColor(Ray ray, Surface world, int depth) {

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
            IO.println("total miss (" + startRay + ")");
            var r = startRay;
            for (int i = 0; i < 10; i++) {
                var th = world.hitRay(r);
                if (th.isEmpty()) {
                    IO.println("ESCAPED");
                    break;
                } else {
                    r = th.get().scatter().get2();
                    IO.println("    " + r);
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
