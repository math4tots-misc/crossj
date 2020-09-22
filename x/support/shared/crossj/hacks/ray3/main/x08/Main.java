package crossj.hacks.ray3.main.x08;

import crossj.IO;
import crossj.List;
import crossj.M;
import crossj.Rand;
import crossj.Time;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.Ray;
import crossj.hacks.ray3.geo.Sphere;
import crossj.hacks.ray3.geo.Surface;
import crossj.hacks.ray3.geo.Surfaces;
import crossj.hacks.ray3.material.Dielectric;
import crossj.hacks.ray3.material.Lambertian;
import crossj.hacks.ray3.material.Material;
import crossj.hacks.ray3.material.Metal;

/**
 * Putting it together with things so far from "Ray Tracing in One Weekend"
 */
public final class Main {
    public static void main(String[] args) {
        var filepath = "out/ray3/x08.bmp";
        var samplesPerPixel = 20;
        var aspectRatio = 3.0 / 2.0;
        var imageWidth = 1200;
        var imageHeight = (int) (imageWidth / aspectRatio);
        var canvas = Bitmap.withDimensions(imageWidth, imageHeight);
        var maxDepth = 200;
        var rng = Rand.getDefault();

        var lookFrom = Matrix.point(13, 2, 3);
        var lookAt = Matrix.point(0, 0, 0);
        var viewUp = Matrix.vector(0, 1, 0);
        var aperture = 0.1;
        // var distanceToFocus = lookFrom.subtract(lookAt).magnitude();
        var distanceToFocus = 10.0;

        var camera = Camera.withDepthOfField(lookFrom, lookAt, viewUp, M.TAU / 18, aspectRatio, aperture,
                distanceToFocus);
        var world = randomScene();

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
        IO.println("Writing to " + filepath);

        IO.writeFileBytes(filepath, canvas.toBMPBytes());
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

    public static Surface randomScene() {
        var rng = Rand.getDefault();
        var surfaces = List.<Surface>of();

        // ground
        surfaces.add(Sphere.withMaterial(Lambertian.withColor(Color.rgb(0.5, 0.5, 0.5)))
                .andTransform(Matrix.scaling(1000, 1000, 1000).thenTranslate(0, -1000, 0)));

        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                var chooseMaterial = rng.next();
                var center = Matrix.point(a + 0.9 * rng.next(), 0.2, b + 0.9 * rng.next());
                var sphereTransform = Matrix.scaling(0.2, 0.2, 0.2).thenTranslate(center.getX(), center.getY(),
                        center.getZ());

                if (center.subtract(Matrix.point(4, 0.2, 0)).magnitude() > 0.9) {
                    Material sphereMaterial;

                    if (chooseMaterial < 0.8) {
                        // diffuse
                        var albedo = Color.rgb(rng.next(), rng.next(), rng.next());
                        sphereMaterial = Lambertian.withColor(albedo);
                    } else if (chooseMaterial < 0.95) {
                        // metal
                        var albedo = Color.rgb(rng.nextDouble(0.5, 1), rng.nextDouble(0.5, 1), rng.nextDouble(0.5, 1));
                        var fuzz = rng.next() * 0.5;
                        sphereMaterial = Metal.withColor(albedo).andFuzz(fuzz);
                    } else {
                        // glass
                        sphereMaterial = Dielectric.withRefractiveIndex(1.5);
                    }

                    surfaces.add(Sphere.withMaterial(sphereMaterial).andTransform(sphereTransform));
                }
            }
        }

        surfaces.add(
                Sphere.withMaterial(Dielectric.withRefractiveIndex(1.5)).andTransform(Matrix.translation(0, 1, 0)));
        surfaces.add(Sphere.withMaterial(Lambertian.withColor(Color.rgb(0.4, 0.2, 0.1)))
                .andTransform(Matrix.translation(-4, 1, 0)));
        surfaces.add(Sphere.withMaterial(Metal.withColor(Color.rgb(0.7, 0.6, 0.5)))
                .andTransform(Matrix.translation(4, 1, 0)));

        return Surfaces.fromIterable(surfaces);
    }
}
