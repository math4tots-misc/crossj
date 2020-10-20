package crossj.hacks.ray.main.y04;

import crossj.base.IO;
import crossj.base.List;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.Box;
import crossj.hacks.ray.RayTracer;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.ObjLoader;
import crossj.hacks.ray.geo.Sphere;
import crossj.hacks.ray.geo.Surfaces;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Metal;
import crossj.hacks.ray.math.Matrix;

/**
 * Playing with support for *.obj files.
 */
public final class Main1 {
    private static final String filepath = "out/ray3/y04-1.bmp";

    public static void main(String[] args) {
        var objLoader = ObjLoader.usingMaterial(Metal.withColor(Color.rgb(0.9, 0.9, 0.9)));
        var camera = Camera.of(
                // lookFrom
                Matrix.point(0, 4, 4),
                // lookAt
                Matrix.point(0, 0, 0),
                // viewUp
                Matrix.vector(0, 1, 0),
                // vfov (vertical field of view)
                Camera.DEFAULT_FIELD_OF_VIEW, Camera.DEFAULT_ASPECT_RATIO);
        var tracer = RayTracer.getDefault().withCamera(camera).withVerbose(true);
        var scene = Surfaces.fromIterable(List.of(
                // // draw the positive axes (x-r, y-g, z-b)
                // Box.withMaterial(Lambertian.withColor(Color.rgb(0.8, 0.2, 0.2)))
                //         .andTransform(Matrix.scaling(0.2, 0.2, 100).thenTranslate(0, 0, 50)),
                // Box.withMaterial(Lambertian.withColor(Color.rgb(0.2, 0.8, 0.2)))
                //         .andTransform(Matrix.scaling(0.2, 100, 0.2).thenTranslate(0, 50, 0)),
                // Box.withMaterial(Lambertian.withColor(Color.rgb(0.2, 0.2, 0.8)))
                //         .andTransform(Matrix.scaling(100, 0.2, 0.2).thenTranslate(50, 0, 0)),

                // draw other things
                // Box.withMaterial(Lambertian.withColor(Color.rgb(0, 0, 0.4)))
                //         .andTransform(Matrix.scaling(0.2, 0.2, 0.2).thenTranslate(-2, 2, -4)),
                // Triangle.withMaterial(Lambertian.withColor(Color.rgb(0.2, 0.2, 0.2))).andAt(Matrix.point(0, 0, -4),
                //         Matrix.point(-2, 2, -4), Matrix.point(-4, 0, -4)),
                // Triangle.withMaterial(Lambertian.withColor(Color.rgb(0.7, 0.2, 0.2))).andAt(Matrix.point(0, 0, -4),
                //         Matrix.point(-4, 0, -4), Matrix.point(-2, -2, -4)),

                Sphere.withMaterial(Metal.withColor(Color.rgb(0.2, 0.4, 0.4)))
                    .andTransform(Matrix.scaling(2, 2, 2).thenTranslate(4, 2, -4)),

                Box.withMaterial(Metal.withColor(Color.rgb(0.4, 0.2, 0.2)).andFuzz(0.1))
                    .andTransform(Matrix.scaling(1, 1, 1).thenTranslate(0, 0.5, -4)),

                Box.withMaterial(Metal.withColor(Color.rgb(0.4, 0.2, 0.2)).andFuzz(0.1))
                    .andTransform(Matrix.scaling(2, 2, 2).thenTranslate(-3, 1, -4)),

                // Triangle.withMaterial(Metal.withColor(Color.rgb(0.9, 0.9, 0.9)))
                //     .andAt(Matrix.point(4, 0, -10), Matrix.point(0, 4, -10), Matrix.point(-4, 0, -10)),

                objLoader.parseString(
                    "v 4 0 -10\n" +
                    "v 0 4 -10\n" +
                    "v -4 0 -10\n" +
                    "v -4 0 -10\n" +
                    "v -8 4 -8\n" +
                    "v -12 0 -6\n" +
                    "f 1 2 3\n" +
                    "f 4 5 6\n"
                ),

                // Triangle.withMaterial(Lambertian.withColor(Color.rgb(0.6, 0.6, 0.6)))
                //     .andAt(Matrix.point(0, 0, 0), Matrix.point(4, 0, 0), Matrix.point(0, 4, 0))));

                // ground
                Sphere.withMaterial(Lambertian.withColor(Color.rgb(0.8, 0.8, 0.4)))
                        .andTransform(Matrix.scaling(1000, 1000, 1000).thenTranslate(0, -1000, -1))));

        IO.println("Starting render");
        tracer.renderToBitmapFile(scene, filepath);
        IO.println("Wrote to file " + filepath);
    }
}
