package crossj.hacks.ray2.geo;

import crossj.Assert;
import crossj.hacks.ray.Matrix;

public final class Intersection {
    private final double t;
    private final Matrix point;
    private final Matrix normal;

    private Intersection(double t, Matrix point, Matrix normal) {
        Assert.withMessage(point.isPoint(), "An intersection point must be a vector");
        Assert.withMessage(normal.isVector(), "An intersection's normal must be a vector");
        this.t = t;
        this.point = point;
        this.normal = normal;
    }

    public static Intersection of(double t, Matrix point, Matrix normal) {
        return new Intersection(t, point, normal);
    }

    /**
     * The distance along the ray (potentially negative) where
     * this intersectino happened
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

    @Override
    public String toString() {
        return "Intersection.of(" + t + ", " + normal + ")";
    }
}
