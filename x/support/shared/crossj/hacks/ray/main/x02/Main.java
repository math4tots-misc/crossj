package crossj.hacks.ray.main.x02;

import crossj.IO;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Sphere;
import crossj.hacks.ray.geo.Surface;
import crossj.hacks.ray.geo.Surfaces;
import crossj.hacks.ray.math.Matrix;

public final class Main {
    public static void main(String[] args) {
        var aspectRatio = Camera.DEFAULT_ASPECT_RATIO;
        var imageWidth = 400;
        var imageHeight = (int) (imageWidth / aspectRatio);
        var canvas = Bitmap.withDimensions(imageWidth, imageHeight);
        var maxDepth = 20;

        var camera = Camera.getDefault();
        var world = Surfaces.of(Sphere.withTransform(Matrix.scaling(0.5, 0.5, 0.5).thenTranslate(0, 0, -1)),
                Sphere.withTransform(Matrix.scaling(100, 100, 100).thenTranslate(0, -100.5, -1)));

        for (int y = imageHeight - 1; y >= 0; y--) {
            for (int x = 0; x < imageWidth; x++) {
                var u = ((double) x) / (imageWidth - 1);
                var v = ((double) y) / (imageHeight - 1);
                var r = camera.getRay(u, v);
                var color = rayColor(r, world, maxDepth);

                // Bitmap's (0, 0) is the upper-left corner, so the y coordinates
                // have to be flipped
                canvas.setColor(x, imageHeight - 1 - y, color);
            }
        }

        IO.writeFileBytes("out/ray3/x02.bmp", canvas.toBMPBytes());
    }

    public static Color rayColor(Ray ray, Surface world, int depth) {
        var tryHit = world.hitRay(ray);

        if (depth <= 0) {
            return Color.BLACK;
        }

        if (tryHit.isPresent()) {
            var hit = tryHit.get();
            var pair = hit.scatter();
            var colorAttenuation = pair.get1();
            var scatteredRay = pair.get2();
            return rayColor(scatteredRay, world, depth - 1).multiply(colorAttenuation);
        }

        // ray is going off into the world
        // assume a blue-ish sky
        var unitDirection = ray.getDirection().normalize();
        var t = 0.5 * (unitDirection.getY() + 1);
        return Color.WHITE.scale(1 - t).add(Color.rgb(0.5, 0.7, 1.0).scale(t));
    }
}
