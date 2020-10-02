package crossj.hacks.ray.main.ch06;

import crossj.IO;
import crossj.Time;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.DeprecatedMaterial;
import crossj.hacks.ray.geo.DeprecatedPointLight;
import crossj.hacks.ray.geo.DeprecatedRay;
import crossj.hacks.ray.geo.DeprecatedSphere;
import crossj.hacks.ray.math.Matrix;

public final class Main {
    public static void main(String[] args) {
        var rayOrigin = Matrix.point(0, 0, -5);
        var wallZ = 10.0;
        var wallSize = 7.0;
        int canvasPixels = 1000;
        var pixelSize = wallSize / canvasPixels;
        var half = wallSize / 2;
        var canvas = Bitmap.withDimensions(canvasPixels, canvasPixels);
        var shape = DeprecatedSphere.unit();
        shape.setMaterial(DeprecatedMaterial.getDefault().withColor(Color.rgb(1, 0.2, 1)));
        var lightPosition = Matrix.point(-10, 10, -10);
        var lightColor = Color.rgb(1, 1, 1);
        var light = DeprecatedPointLight.of(lightPosition, lightColor);

        var start = Time.now();
        for (int y = 0; y < canvasPixels; y++) {
            var worldY = half - pixelSize * y;
            for (int x = 0; x < canvasPixels; x++) {
                var worldX = -half + pixelSize * x;
                var position = Matrix.point(worldX, worldY, wallZ);

                var r = DeprecatedRay.of(rayOrigin, position.subtract(rayOrigin).normalize());
                var xs = shape.intersectRay(r);

                if (xs.getHit().isPresent()) {
                    var hit = xs.getHit().get();
                    var point = r.position(hit.getT());
                    var normal = hit.getObject().normalAt(point);
                    var eye = r.getDirection().negate();
                    var color = hit.getObject().getMaterial().lighting(light, point, eye, normal);
                    canvas.setColor(x, y, color);
                }
            }
        }
        var end = Time.now();
        IO.println("start = " + start);
        IO.println("end = " + end);
        IO.println("Rendering took " + (end - start) + " seconds");

        IO.writeFileBytes("out/ray/ch06.bmp", canvas.toBMPBytes());
    }
}
