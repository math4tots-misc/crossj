package crossj.hacks.ray3.main.y02;

import crossj.IO;
import crossj.List;
import crossj.hacks.image.Bitmap;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.RayTracer;
import crossj.hacks.ray3.geo.AABB;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.SDF;
import crossj.hacks.ray3.geo.Surface;
import crossj.hacks.ray3.geo.Surfaces;

public final class Main {

    private static final String filepath = "out/ray3/y02.bmp";

    public static void main(String[] args) {
        var lookFrom = Matrix.point(0, 3, 4);
        var lookAt = Matrix.point(0, 0, 0);
        var viewUp = Matrix.vector(0, 1, 0);
        var camera = Camera.of(lookFrom, lookAt, viewUp, Camera.DEFAULT_FIELD_OF_VIEW, Camera.DEFAULT_ASPECT_RATIO);

        var tracer = RayTracer.getDefault().withVerbose(true).withSamplesPerPixel(20).withCamera(camera);
        var height = tracer.getImageHeight();
        var scene = buildScene();
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

    private static Surface buildScene() {
        var list = List.<Surface>of();
        // var r = 2;
        // list.add(Sphere.withTransform(Matrix.scaling(r, r, r)));
        // list.addAll(List.of(SDF.fromDistanceEstimator(point -> point.withW(0).magnitude() - r)
        //         .withBox(AABB.withPoints(Matrix.point(-r, -r, -r), Matrix.point(r, r, r)))));

        // list.add(SDF.fromDistanceEstimator(
        //         point -> Matrix.vector(point.getX(), point.getY(), point.getZ()).magnitude() - r)
        //         .withBox(AABB.withPoints(Matrix.point(-r * 2, -r * 2, -r * 2), Matrix.point(r * 2, r * 2, r * 2))));

        var n = Matrix.vector(0, 0, 1).normalize();
        list.add(SDF.fromDistanceEstimator(point -> point.dot(n))
            .withBox(AABB.withPoints(Matrix.point(-6, -6, -1), Matrix.point(6, 6, 1))));

        // list.add(SDF.fromDistanceEstimator(
        //         point -> {
        //             var mag2 = 0.0;
        //             for (int a = 0; a < 3; a++) {
        //                 var x = point.get(a, 0);
        //                 var low = M.floor(x);
        //                 var high = M.ceil(x);
        //                 var dist2 = M.min(M.abs(x - low), M.abs(x - high));
        //                 mag2 += dist2;
        //             }
        //             return M.sqrt(mag2) - 0.4;
        //         })
        //         .withBox(AABB.withPoints(Matrix.point(-10, -10, -10), Matrix.point(0, 0, 0))));
        // list.add(Sphere.withMaterial(Lambertian.withColor(Color.rgb(0.2, 0.2, 0.1)))
        //         .andTransform(Matrix.scaling(1000, 1000, 1000).thenTranslate(0, -1000 - r, 0)));

        return Surfaces.fromIterable(list);
    }
}
