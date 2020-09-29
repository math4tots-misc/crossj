package crossj.hacks.ray.material;

import crossj.Pair;
import crossj.Rand;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.geo.Ray;

/**
 * Mix between Lambertian and Metal
 */
public final class Glossy implements Material {
    private final double shininess;
    private final Metal metal;
    private final Lambertian lambertian;

    private Glossy(double shininess, Metal metal, Lambertian lambertian) {
        this.shininess = shininess;
        this.metal = metal;
        this.lambertian = lambertian;
    }

    public static Glossy fromParts(double shininess, Metal metal, Lambertian lambertian) {
        return new Glossy(shininess, metal, lambertian);
    }

    public Glossy setShininess(double shininess) {
        return new Glossy(shininess, metal, lambertian);
    }

    public Glossy setReflectiveAlbedo(Color albedo) {
        return new Glossy(shininess, Metal.withColor(albedo).andFuzz(metal.getFuzz()), lambertian);
    }

    public Glossy setDiffuseAlbedo(Color albedo) {
        return new Glossy(shininess, metal, Lambertian.withColor(albedo));
    }

    @Override
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal, boolean front) {
        if (Rand.getDefault().next() < shininess) {
            return metal.scatter(inputRay, point, normal, front);
        } else {
            return lambertian.scatter(inputRay, point, normal, front);
        }
    }

    @Override
    public String toString() {
        return "Glossy.fromParts(" + shininess + ", " + metal + ", " + lambertian + ")";
    }

    public Material simplify() {
        if (shininess == 1.0) {
            return metal;
        } else if (shininess == 0.0) {
            return lambertian;
        } else {
            return this;
        }
    }
}
