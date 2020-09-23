package crossj.hacks.ray3.main.y01;

import crossj.IO;
import crossj.hacks.image.Bitmap;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.RayTracer;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.Sphere;
import crossj.hacks.ray3.geo.Surfaces;

public final class Main {

    private static final String filepath = "out/ray3/y01.bmp";

    public static void main(String[] args) {
        var lookFrom = Matrix.point(0, 4, 2);
        var lookAt = Matrix.point(0, 0, -4);
        var viewUp = Matrix.vector(0, 1, 0);
        var camera = Camera.of(lookFrom, lookAt, viewUp, Camera.DEFAULT_FIELD_OF_VIEW, Camera.DEFAULT_ASPECT_RATIO);

        var tracer = RayTracer.getDefault().withVerbose(true).withSamplesPerPixel(20).withCamera(camera);
        var height = tracer.getImageHeight();
        var scene = Surfaces.of(Sphere.withTransform(Matrix.translation(0, 1, -4)),
                Sphere.withTransform(Matrix.scaling(1000, 1000, 1000).thenTranslate(0, -1000, 0)));
        var canvas = Bitmap.withDimensions(tracer.getImageWidth(), tracer.getImageHeight());

        IO.println("Starting render");
        tracer.render(scene, (x, y, color) -> {
            // image needs to be flipped, due to coordinate system of canvas vs RayTracer
            canvas.setColor(x, height - 1 - y, color);
            return null;
        });

        IO.println("Writing image out to " + filepath);
        IO.writeFileBytes(filepath, canvas.toBMPBytes());
    }
}
