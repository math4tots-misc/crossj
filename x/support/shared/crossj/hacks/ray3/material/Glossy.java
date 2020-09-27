package crossj.hacks.ray3.material;

import crossj.Pair;
import crossj.Rand;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Ray;

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

    @Override
    public Pair<Color, Ray> scatter(Ray inputRay, Matrix point, Matrix normal, boolean front) {
        if (Rand.getDefault().next() < shininess) {
            return metal.scatter(inputRay, point, normal, front);
        } else {
            return lambertian.scatter(inputRay, point, normal, front);
        }
    }
}
