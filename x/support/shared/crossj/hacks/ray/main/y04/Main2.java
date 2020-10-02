package crossj.hacks.ray.main.y04;

import crossj.IO;
import crossj.hacks.image.Color;
import crossj.hacks.ray.RayTracer;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.ObjLoader;
import crossj.hacks.ray.material.Metal;
import crossj.hacks.ray.math.Matrix;

/**
 * Playing with support for *.obj files.
 */
public final class Main2 {
    private static final String filepath = "out/ray3/y04-2.bmp";

    public static void main(String[] args) {
        // var objLoader = ObjLoader.usingMaterial(Metal.withColor(Color.rgb(0.9, 0.9, 0.9)));
        var objLoader = ObjLoader.usingMaterial(Metal.withColor(Color.rgb(0.6, 0.4, 0.4)));
        var camera = Camera.of(
                // lookFrom
                // Matrix.point(0, 4, 4),
                Matrix.point(5, 4, 3),
                // lookAt
                Matrix.point(0, 0, 0),
                // viewUp
                Matrix.vector(0, 1, 0),
                // vfov (vertical field of view)
                Camera.DEFAULT_FIELD_OF_VIEW, Camera.DEFAULT_ASPECT_RATIO);
        var tracer = RayTracer.getDefault().withCamera(camera).withVerbose(true);
        var scene = objLoader.load("data/ray/teapot.obj");

        IO.println("Starting render");
        tracer.renderToBitmapFile(scene, filepath);
        IO.println("Wrote to file " + filepath);
    }
}
