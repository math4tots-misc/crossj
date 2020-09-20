package crossj.hacks.ray3.geo;

import crossj.Pair;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;

/**
 * Contextual information about when a ray hits a surface.
 */
public final class Hit {
    private final Ray ray;
    private final double t;
    private final Matrix point;
    private final Matrix normal;
    private final Material material;

    private Hit(Ray ray, double t, Matrix point, Matrix normal, Material material) {
        this.ray = ray;
        this.t = t;
        this.point = point;
        this.normal = normal;
        this.material = material;
    }

    public static Hit of(Ray ray, double t, Matrix point, Matrix normal, Material material) {
        return new Hit(ray, t, point, normal, material);
    }

    /**
     * The distance along the ray (potentially negative) where
     * this intersection happened
     */
    public double getT() {
        return t;
    }

    /**
     * The point where this intersection takes place
     */
    public Matrix getPoint() {
        return point;
    }

    /**
     * The normal vector of the surface where the intersection happened
     */
    public Matrix getNormal() {
        return normal;
    }

    /**
     * The material that was hit
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Simulates scattering by returning a pair:
     *      1: the color attenuation (i.e. how much of the light did this surface absorb?)
     *      2: the scattered ray (i.e. in what direction was light bent?)
     */
    public Pair<Color, Ray> scatter() {
        return material.scatter(ray, point, normal);
    }
}
