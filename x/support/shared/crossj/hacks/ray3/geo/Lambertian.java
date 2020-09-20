package crossj.hacks.ray3.geo;

import crossj.Pair;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;

public final class Lambertian implements Material {
    private final Color albedo;

    private Lambertian(Color albedo) {
        this.albedo = albedo;
    }

    public static Lambertian withColor(Color albedo) {
        return new Lambertian(albedo);
    }

    @Override
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal) {
        return Pair.of(albedo, Ray.of(point, normal.add(Sphere.randomUnitVector())));
    }
}
