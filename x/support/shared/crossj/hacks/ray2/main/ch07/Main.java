package crossj.hacks.ray2.main.ch07;

import crossj.IO;
import crossj.List;
import crossj.Rand;
import crossj.Time;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray2.geo.Camera;
import crossj.hacks.ray2.geo.Ray;
import crossj.hacks.ray2.geo.Sphere;
import crossj.hacks.ray2.geo.Surface;
import crossj.hacks.ray2.geo.Surfaces;

/**
 * Anti-aliasing
 */
public final class Main {

    private static Color rayColor(Ray r, Surface world) {
        var intersections = world.intersectRay(r);
        if (intersections.getHit().isPresent()) {
            var hit = intersections.getHit().get();
            var normal = hit.getNormal();
            return Color.rgb(normal.getX(), normal.getY(), normal.getZ()).add(Color.WHITE).scale(0.5);
        }
        var unitDirection = r.getDirection().normalize();
        var t = 0.5 * (unitDirection.getY() + 1.0);
        return Color.rgb(1, 1, 1).scale(1.0 - t).add(Color.rgb(0.5, 0.7, 1).scale(t));
    }

    public static void main(String[] args) {
        var rng = Rand.getDefault();

        // Image
        var aspectRatio = Camera.DEFAULT_ASPECT_RATIO;
        var imageWidth = 400;
        var imageHeight = (int) (imageWidth / aspectRatio);
        var samplesPerPixel = 100;

        // World
        var world = Surfaces.of(
            Sphere.withTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(0, 0, -1)),
            Sphere.withTransform(Matrix.scaling(100, 100, 100).thenTranslate(0, -100.5, -1))
        );

        // Camera
        var camera = Camera.getDefault();

        // Render
        IO.println("Starting render");
        var start = Time.now();
        Bitmap canvas = Bitmap.withDimensions(imageWidth, imageHeight);
        for (int j = 0; j < imageHeight; j++) {
            for (int i = 0; i < imageWidth; i++) {

                var samples = List.<Color>of();
                for (int s = 0; s < samplesPerPixel; s++) {
                    var u = (i + rng.next()) / (imageWidth - 1);
                    var v = (j + rng.next()) / (imageHeight - 1);
                    var r = camera.getRay(u, v);
                    samples.add(rayColor(r, world));
                }

                var pixelColor = Color.join(samples);

                // the coordinates are actually flipped...
                canvas.setColor(i, imageHeight - 1 - j, pixelColor);
            }
        }
        var end = Time.now();
        IO.println("Finished render in " + (end - start) + " seconds");
        IO.writeFileBytes("out/ray2/ch07.bmp", canvas.toBMPBytes());
    }
}
