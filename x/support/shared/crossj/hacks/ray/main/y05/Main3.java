package crossj.hacks.ray.main.y05;

import crossj.base.IO;
import crossj.hacks.image.Color;
import crossj.hacks.ray.RayTracer;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.Surface;
import crossj.hacks.ray.geo.Surfaces;
import crossj.hacks.ray.geo.Triangle;
import crossj.hacks.ray.material.Glossy;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Metal;
import crossj.hacks.ray.math.Matrix;

/**
 * Limited lighting
 */
public final class Main3 {
    private static final String filepath = "out/ray3/y05-3.bmp";
    // private static final Material wallMaterial = Glossy.fromParts(0.2,
    // Metal.withColor(Color.RED),
    // Lambertian.withColor(Color.GREEN));

    public static void main(String[] args) {
        var lookFrom = Matrix.point(0, 4, 5);
        var lookAt = Matrix.point(0, 4, 0);
        var camera = Camera.basic(lookFrom, lookAt);
        var tracer = RayTracer.getDefault().withCamera(camera).withVerbose(true);

        // var wall1 = newZWall(-2);
        // // Triangle.withMaterial(wallMaterial).andAt(
        // // Matrix.point( 20, -20, -2),
        // // Matrix.point( 20, 20, -2),
        // // Matrix.point(-20, 20, -2)
        // // );

        var scene = Surfaces.of(
                // newZWall(-15),
                // newXWall(-20),

                // floor
                newYWall(-2)
        // , newZWall(15)
        // , newXWall(15)
        // , newXWall(-15)
        );

        IO.println("scene = " + scene);

        IO.println("Starting render");
        tracer.renderToBitmapFile(scene, filepath);
        IO.println("Wrote to file " + filepath);
    }

    // private static Surface newZWall(double z) {
    //     var red = Color.rgb(0.6, 0.2, 0.2);
    //     var wallMaterial = Glossy.fromParts(0.5, Metal.withColor(red), Lambertian.withColor(red));
    //     double minX = -10000;
    //     double minY = -10000;
    //     double maxX = 10000;
    //     double maxY = 10000;
    //     return Surfaces.of(
    //             Triangle.withMaterial(wallMaterial).andAt(Matrix.point(maxX, minY, z), Matrix.point(maxX, maxY, z),
    //                     Matrix.point(minX, maxY, z)),
    //             Triangle.withMaterial(wallMaterial).andAt(Matrix.point(maxX, minY, z), Matrix.point(minX, maxY, z),
    //                     Matrix.point(minX, minY, z)));
    // }

    // private static Surface newXWall(double x) {
    //     var blue = Color.rgb(0.2, 0.2, 0.6);
    //     var wallMaterial = Glossy.fromParts(0.5, Metal.withColor(blue), Lambertian.withColor(blue));
    //     double minZ = -10000;
    //     double minY = -10000;
    //     double maxZ = 10000;
    //     double maxY = 10000;
    //     return Surfaces.of(
    //             Triangle.withMaterial(wallMaterial).andAt(Matrix.point(x, minY, maxZ), Matrix.point(x, maxY, maxZ),
    //                     Matrix.point(x, maxY, minZ)),
    //             Triangle.withMaterial(wallMaterial).andAt(Matrix.point(x, minY, maxZ), Matrix.point(x, maxY, minZ),
    //                     Matrix.point(x, minY, maxZ)));
    // }

    private static Surface newYWall(double y) {
        var blue = Color.rgb(0.2, 0.6, 0.2);
        var wallMaterial = Glossy.fromParts(0.5, Metal.withColor(blue), Lambertian.withColor(blue));
        double minX = -10000;
        double minZ = -10000;
        double maxX = 10000;
        double maxZ = 10000;
        return Surfaces.of(
                Triangle.withMaterial(wallMaterial).andAt(Matrix.point(minX, y, maxZ), Matrix.point(maxX, y, maxZ),
                        Matrix.point(maxX, y, minZ)),
                Triangle.withMaterial(wallMaterial).andAt(Matrix.point(minX, y, maxZ), Matrix.point(maxX, y, minZ),
                        Matrix.point(minX, y, maxZ)));
    }
}
