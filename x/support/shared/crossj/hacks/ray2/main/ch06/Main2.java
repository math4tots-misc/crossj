package crossj.hacks.ray2.main.ch06;

import crossj.IO;
import crossj.Time;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray2.geo.Ray;
import crossj.hacks.ray2.geo.Sphere;
import crossj.hacks.ray2.geo.Surface;
import crossj.hacks.ray2.geo.Surfaces;

public final class Main2 {

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
        // Image
        var aspectRatio = 16.0 / 9.0;
        var imageWidth = 400;
        var imageHeight = (int) (imageWidth / aspectRatio);

        // World
        var world = Surfaces.of(
            Sphere.withTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(0, 0, -1)),
            Sphere.withTransform(Matrix.scaling(100, 100, 100).thenTranslate(0, -100.5, -1))
        );

        // Camera

        var viewportHeight = 2.0;
        var viewportWidth = aspectRatio * viewportHeight;
        var focalLength = 1.0;

        var origin = Matrix.point(0, 0, 0);
        var horizontal = Matrix.vector(viewportWidth, 0, 0);
        var vertical = Matrix.vector(0, viewportHeight, 0);
        var lowerLeftCorner = origin.subtract(horizontal.scale(0.5)).subtract(vertical.scale(0.5))
                .subtract(Matrix.vector(0, 0, focalLength));

        // Render
        IO.println("Starting render");
        var start = Time.now();
        Bitmap canvas = Bitmap.withDimensions(imageWidth, imageHeight);
        for (int j = 0; j < imageHeight; j++) {
            for (int i = 0; i < imageWidth; i++) {
                var u = ((double) i) / (imageWidth - 1);
                var v = ((double) j) / (imageHeight - 1);
                var r = Ray.of(origin,
                        lowerLeftCorner.add(horizontal.scale(u)).add(vertical.scale(v).subtract(origin)));
                var pixelColor = rayColor(r, world);

                // the coordinates are actually flipped...
                canvas.setColor(i, imageHeight - 1 - j, pixelColor);
            }
        }
        var end = Time.now();
        IO.println("Finished render in " + (end - start) + " seconds");
        IO.writeFileBytes("out/ray2/ch06-2.bmp", canvas.toBMPBytes());
    }
}
