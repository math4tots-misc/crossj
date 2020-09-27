package crossj.hacks.ray3.main.y05;

import crossj.IO;
import crossj.M;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.RayTracer;
import crossj.hacks.ray3.geo.Box;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.Sphere;
import crossj.hacks.ray3.geo.Surfaces;
import crossj.hacks.ray3.material.Glossy;
import crossj.hacks.ray3.material.Lambertian;
import crossj.hacks.ray3.material.Metal;

/**
 * Glossy
 */
public final class Main1 {
    private static final String filepath = "out/ray3/y05-1.bmp";

    public static void main(String[] args) {
        var lookFrom = Matrix.point(0, 4, 7);
        var lookAt = Matrix.point(0, 0, 0);

        var camera = Camera.basic(lookFrom, lookAt);

        var tracer = RayTracer.getDefault().withCamera(camera).withVerbose(true);

        var scene = Surfaces.of(Box
                .withMaterial(Glossy.fromParts(0.9, Metal.withColor(Color.rgb(0.9, 0.8, 0.8)),
                        Lambertian.withColor(Color.rgb(0.5, 0.6, 0.7))))
                .andTransform(Matrix.identity(4).thenScale(8, 8, 8).thenRotateX(M.TAU / 16).thenTranslate(0, 4, -7.5)),
                Box.withMaterial(Glossy.fromParts(0.1, Metal.withColor(Color.rgb(0.2, 0.2, 0.3)),
                        Lambertian.withColor(Color.rgb(0.2, 0.2, 0.3))))
                        .andTransform(Matrix.identity(4).thenScale(2, 2, 2).thenTranslate(-3, 1, 0)),
                // ground
                Sphere.withMaterial(Lambertian.withColor(Color.rgb(0.8, 0.8, 0.4)))
                        .andTransform(Matrix.scaling(1000, 1000, 1000).thenTranslate(0, -1000, 0)));

        IO.println("Starting render");
        tracer.renderToBitmapFile(scene, filepath);
        IO.println("Wrote to file " + filepath);
    }
}
