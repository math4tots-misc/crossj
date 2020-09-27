package crossj.hacks.ray.material;

import crossj.Pair;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Sphere;

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
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal, boolean front) {
        var reflected = inputRay.getDirection().normalize().reflectAround(normal);
        if (fuzz > 0) {
            return Pair.of(albedo, Ray.of(point, reflected.add(Sphere.randomUnitVector().scale(fuzz))));
        } else {
            return Pair.of(albedo, Ray.of(point, reflected));
        }
    }

    public Color getAlbedo() {
        return albedo;
    }

    public double getFuzz() {
        return fuzz;
    }

    @Override
    public String toString() {
        return "Metal.withColor(" + albedo + ").andFuzz(" + fuzz + ")";
    }
}
