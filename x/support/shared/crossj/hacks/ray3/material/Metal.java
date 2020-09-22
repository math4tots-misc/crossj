package crossj.hacks.ray3.material;

import crossj.Pair;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Ray;
import crossj.hacks.ray3.geo.Sphere;

public final class Metal implements Material {
    private final Color albedo;
    private final double fuzz;

    private Metal(Color albedo, double fuzz) {
        this.albedo = albedo;
        this.fuzz = fuzz > 1 ? 1 : fuzz;
    }

    public static Metal withColor(Color albedo) {
        return new Metal(albedo, 0);
    }

    public Metal andFuzz(double fuzz) {
        return new Metal(albedo, fuzz);
    }

    @Override
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal) {
        var reflected = inputRay.getDirection().normalize().reflectAround(normal);
        if (fuzz > 0) {
            return Pair.of(albedo, Ray.of(point, reflected.add(Sphere.randomUnitVector().scale(fuzz))));
        } else {
            return Pair.of(albedo, Ray.of(point, reflected));
        }
    }
}