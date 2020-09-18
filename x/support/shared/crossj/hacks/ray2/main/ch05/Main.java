package crossj.hacks.ray2.main.ch05;

import crossj.IO;
import crossj.Time;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Sphere;

public final class Main {

    private final static Sphere sphere = Sphere.withTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(0, 0, -1));

    private static Color rayColor(Ray r) {
        if (sphere.intersectRay(r).getHit().isPresent()) {
            return Color.RED;
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
                var pixelColor = rayColor(r);

                // the coordinates are actually flipped...
                canvas.setColor(i, imageHeight - 1 - j, pixelColor);
            }
        }
        var end = Time.now();
        IO.println("Finished render in " + (end - start) + " seconds");
        IO.writeFileBytes("out/ray2/ch05.bmp", canvas.toBMPBytes());
    }
}
