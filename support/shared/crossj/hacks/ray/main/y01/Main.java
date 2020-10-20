package crossj.hacks.ray.main.y01;

import crossj.base.IO;
import crossj.base.List;
import crossj.base.Rand;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.Box;
import crossj.hacks.ray.RayTracer;
import crossj.hacks.ray.geo.Camera;
import crossj.hacks.ray.geo.Sphere;
import crossj.hacks.ray.geo.Surface;
import crossj.hacks.ray.geo.Surfaces;
import crossj.hacks.ray.material.Dielectric;
import crossj.hacks.ray.material.Lambertian;
import crossj.hacks.ray.material.Material;
import crossj.hacks.ray.material.Metal;
import crossj.hacks.ray.math.Matrix;

public final class Main {

    private static final String filepath = "out/ray3/y01.bmp";

    public static void main(String[] args) {
        var lookFrom = Matrix.point(0, 3, 1);
        var lookAt = Matrix.point(0, 0, -4);
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
        var rng = Rand.getDefault();
        var list = List.<Surface>of();
        list.addAll(List.of(
                Sphere.withMaterial(Metal.withColor(Color.rgb(0.4, 0.4, 0.1)))
                        .andTransform(Matrix.translation(0, 1, -4)),
                Box.withMaterial(Lambertian.withColor(Color.rgb(0.4, 0.2, 0.2)))
                        .andTransform(Matrix.translation(-2, 0.5, -4)),
                Box.withMaterial(Dielectric.withRefractiveIndex(1.5)).andTransform(Matrix.translation(2, 0.5, -4)),
                Sphere.withMaterial(Dielectric.withRefractiveIndex(1.5)).andTransform(Matrix.translation(4.5, 1, -3.7)),
                Sphere.withTransform(Matrix.scaling(1000, 1000, 1000).thenTranslate(0, -1000, 0))));

        for (int a = 3; a < 6; a++) {
            for (int b = 3; b < 6; b++) {
                var chooseMaterial = rng.next();
                var center = Matrix.point(-a + 0.9 * rng.next(), 0.2, -b + 0.9 * rng.next());
                var chosenTransform = Matrix.scaling(0.2, 0.2, 0.2).thenTranslate(center.getX(), center.getY(),
                        center.getZ());

                if (center.subtract(Matrix.point(4, 0.2, 0)).magnitude() > 0.9) {
                    Material chosenMaterial;

                    if (chooseMaterial < 0.8) {
                        // diffuse
                        var albedo = Color.rgb(rng.next(), rng.next(), rng.next());
                        chosenMaterial = Lambertian.withColor(albedo);
                    } else if (chooseMaterial < 0.95) {
                        // metal
                        var albedo = Color.rgb(rng.nextDouble(0.5, 1), rng.nextDouble(0.5, 1), rng.nextDouble(0.5, 1));
                        var fuzz = rng.next() * 0.5;
                        chosenMaterial = Metal.withColor(albedo).andFuzz(fuzz);
                    } else {
                        // glass
                        chosenMaterial = Dielectric.withRefractiveIndex(1.5);
                    }

                    if (rng.next() < 0.5) {
                        list.add(Sphere.withMaterial(chosenMaterial).andTransform(chosenTransform));
                    } else {
                        list.add(Box.withMaterial(chosenMaterial).andTransform(Matrix.scaling(2, 2, 2))
                                .andTransform(chosenTransform));
                    }
                }
            }
        }

        for (int a = 3; a < 6; a++) {
            for (int b = 5; b < 8; b++) {
                var chooseMaterial = rng.next();
                var center = Matrix.point(a + 0.9 * rng.next(), 0.2, -b + 0.9 * rng.next());
                var chosenTransform = Matrix.scaling(0.2, 0.2, 0.2).thenTranslate(center.getX(), center.getY(),
                        center.getZ());

                if (center.subtract(Matrix.point(4, 0.2, 0)).magnitude() > 0.9) {
                    Material chosenMaterial;

                    if (chooseMaterial < 0.8) {
                        // diffuse
                        var albedo = Color.rgb(rng.next(), rng.next(), rng.next());
                        chosenMaterial = Lambertian.withColor(albedo);
                    } else if (chooseMaterial < 0.95) {
                        // metal
                        var albedo = Color.rgb(rng.nextDouble(0.5, 1), rng.nextDouble(0.5, 1), rng.nextDouble(0.5, 1));
                        var fuzz = rng.next() * 0.5;
                        chosenMaterial = Metal.withColor(albedo).andFuzz(fuzz);
                    } else {
                        // glass
                        chosenMaterial = Dielectric.withRefractiveIndex(1.5);
                    }

                    if (rng.next() < 0.5) {
                        list.add(Sphere.withMaterial(chosenMaterial).andTransform(chosenTransform));
                    } else {
                        list.add(Box.withMaterial(chosenMaterial).andTransform(Matrix.scaling(2, 2, 2))
                                .andTransform(chosenTransform));
                    }
                }
            }
        }

        return Surfaces.fromIterable(list);
    }
}
