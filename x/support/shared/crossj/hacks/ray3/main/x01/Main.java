package crossj.hacks.ray3.main.x01;

import crossj.IO;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.Ray;
import crossj.hacks.ray3.geo.Sphere;
import crossj.hacks.ray3.geo.Surface;
import crossj.hacks.ray3.geo.Surfaces;

/**
 * Return color based on the (x,y,z) values of the normal vector
 */
public final class Main {
    public static void main(String[] args) {
        var aspectRatio = Camera.DEFAULT_ASPECT_RATIO;
        var imageWidth = 400;
        var imageHeight = (int) (imageWidth / aspectRatio);
        var canvas = Bitmap.withDimensions(imageWidth, imageHeight);

        var camera = Camera.getDefault();
        var world = Surfaces.of(Sphere.withTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(0, 0, -1)),
                Sphere.withTransform(Matrix.scaling(100, 100, 100).thenTranslate(0, -100.5, -1)));

        for (int y = imageHeight - 1; y >= 0; y--) {
            for (int x = 0; x < imageWidth; x++) {
                var u = ((double) x) / (imageWidth - 1);
                var v = ((double) y) / (imageHeight - 1);
                var r = camera.getRay(u, v);
                var color = rayColor(r, world);

                // Bitmap's (0, 0) is the upper-left corner, so the y coordinates
                // have to be flipped
                canvas.setColor(x, imageHeight - 1 - y, color);
            }
        }

        IO.writeFileBytes("out/ray3/x01.bmp", canvas.toBMPBytes());
    }

    public static Color rayColor(Ray ray, Surface world) {
        var tryHit = world.hitRay(ray);

        if (tryHit.isPresent()) {
            var hit = tryHit.get();
            var normal = hit.getNormal();
            return Color.rgb(normal.getX(), normal.getY(), normal.getZ()).add(Color.WHITE).scale(0.5);
        }

        // ray is going off into the world
        // assume a blue-ish sky
        var unitDirection = ray.getDirection().normalize();
        var t = 0.5 * (unitDirection.getY() + 1);
        return Color.WHITE.scale(1 - t).add(Color.rgb(0.5, 0.7, 1.0).scale(t));
    }
}
