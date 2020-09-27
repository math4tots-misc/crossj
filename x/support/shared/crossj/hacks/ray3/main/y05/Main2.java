package crossj.hacks.ray3.main.y05;

import crossj.IO;
import crossj.M;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.RayTracer;
import crossj.hacks.ray3.geo.Camera;
import crossj.hacks.ray3.geo.ObjLoader;
import crossj.hacks.ray3.geo.Surfaces;
import crossj.hacks.ray3.geo.Triangle;
import crossj.hacks.ray3.material.Metal;

/**
 * Glossy
 */
public final class Main2 {
    private static final String filepath = "out/ray3/y05-2.bmp";

    public static void main(String[] args) {
        var lookFrom = Matrix.point(-300, -433, 30);
        var lookAt = Matrix.point(45, -433, 30);
        var camera = Camera.basic(lookFrom, lookAt);
        var tracer = RayTracer.getDefault().withCamera(camera).withVerbose(true);

        var objLoader = ObjLoader.getDefault().setVerbose(true);
        objLoader.loadMTL("data/ray/vp.mtl");

        var trumpet = objLoader.load("data/ray/trumpet2.obj");

        var box = trumpet.getBoundingBox();
        IO.println("min = " + box.getMin());
        IO.println("max = " + box.getMax());
        IO.println("center = " + box.getCenter());

        var unrotatedTriangle = Triangle.withMaterial(Metal.withColor(Color.rgb(0.9, 0.9, 0.9)))
                .andAt(Matrix.point(200, -1200, 400), Matrix.point(200, 0, 400), Matrix.point(200, -450, -900));

        var triangle = unrotatedTriangle.andTransform(Matrix.zRotation(M.TAU / 8));

        IO.println("zrotation(0) = " + Matrix.zRotation(0));
        IO.println("unrotated = " + unrotatedTriangle);
        IO.println("rotated = " + triangle);

        var scene = Surfaces.of(
                // giant mirror
                triangle,
                // trumpet
                trumpet
        // // ground
        // , Sphere.withMaterial(Lambertian.withColor(Color.rgb(0.8, 0.8, 0.4)))
        // .andTransform(Matrix.scaling(1000, 1000, 1000).thenTranslate(0, -1000, 0))
        );

        IO.println("Starting render");
        tracer.renderToBitmapFile(scene, filepath);
        IO.println("Wrote to file " + filepath);
    }
}
