package crossj.hacks.ray3.main.y04;

import crossj.IO;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.RayTracer;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.ObjLoader;
import crossj.hacks.ray3.material.Metal;

/**
 * Playing with support for *.obj files.
 */
public final class Main3 {
    private static final String filepath = "out/ray3/y04-3.bmp";

    public static void main(String[] args) {
        // var objLoader = ObjLoader.usingMaterial(Metal.withColor(Color.rgb(0.9, 0.9, 0.9)));
        var objLoader = ObjLoader.usingMaterial(Metal.withColor(Color.rgb(0.6, 0.4, 0.4)));
        var camera = Camera.of(
                // lookFrom
                // Matrix.point(0, 4, 4),
                // Matrix.point(5, 4, 3),
                Matrix.point(7, 4, 7),
                // lookAt
                Matrix.point(0, 0, 0),
                // viewUp
                Matrix.vector(0, 1, 0),
                // vfov (vertical field of view)
                Camera.DEFAULT_FIELD_OF_VIEW, Camera.DEFAULT_ASPECT_RATIO);
        var tracer = RayTracer.getDefault().withCamera(camera).withVerbose(true);
        var scene = objLoader.load("data/ray/cow.obj");

        IO.println("Starting render");
        tracer.renderToBitmapFile(scene, filepath);
        IO.println("Wrote to file " + filepath);
    }
}
