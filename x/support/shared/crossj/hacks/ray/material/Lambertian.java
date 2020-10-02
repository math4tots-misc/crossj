package crossj.hacks.ray.material;

import crossj.Pair;
import crossj.hacks.image.Color;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Sphere;
import crossj.hacks.ray.math.Matrix;

public final class Lambertian implements Material {
    private final Color albedo;

    private Lambertian(Color albedo) {
        this.albedo = albedo;
    }

    public static Lambertian withColor(Color albedo) {
        return new Lambertian(albedo);
    }

    @Override
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal, boolean front) {
        return Pair.of(albedo, Ray.of(point, normal.add(Sphere.randomUnitVector())));
    }

    @Override
    public String toString() {
        return "Lambertian.withColor(" + albedo + ")";
    }
}
