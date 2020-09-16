package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.M;
import crossj.hacks.ray.Matrix;

public final class Sphere {
    private final Matrix origin;
    private final double radius;

    private Sphere(Matrix origin, double radius) {
        Assert.withMessage(origin.isPoint(), "A sphere's origin must be a point");
        this.origin = origin;
        this.radius = radius;
    }

    /**
     * Create a unit sphere centered at the origin
     */
    public static Sphere unit() {
        return new Sphere(Matrix.point(0, 0, 0), 1);
    }

    public Intersections intersectRay(Ray ray) {
        // vector from the sphere's center, to the ray origin
        Matrix sphereToRay = ray.getOrigin().subtract(origin);
        double a = ray.getDirection().dot(ray.getDirection());
        double b = 2 * ray.getDirection().dot(sphereToRay);
        double c = sphereToRay.dot(sphereToRay) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return Intersections.of();
        } else {
            double t1 = (-b - M.sqrt(discriminant)) / (2 * a);
            double t2 = (-b + M.sqrt(discriminant)) / (2 * a);
            return Intersections.of(
                Intersection.of(t1, this),
                Intersection.of(t2, this)
            );
        }
    }
}
