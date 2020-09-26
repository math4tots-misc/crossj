package crossj.hacks.ray3.main.y03;

import crossj.IO;
import crossj.List;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.RayTracer;
import crossj.hacks.ray3.geo.Box;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.Sphere;
import crossj.hacks.ray3.geo.Surfaces;
import crossj.hacks.ray3.geo.Triangle;
import crossj.hacks.ray3.material.Lambertian;
import crossj.hacks.ray3.material.Metal;

public final class Main {
    private static final String filepath = "out/ray3/y03.bmp";

    public static void main(String[] args) {
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

                Triangle.withMaterial(Metal.withColor(Color.rgb(0.9, 0.9, 0.9)))
                    .andAt(Matrix.point(4, 0, -10), Matrix.point(0, 4, -10), Matrix.point(-4, 0, -10)),

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
