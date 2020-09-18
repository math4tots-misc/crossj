package crossj.hacks.ray2.geo;

import crossj.Assert;
import crossj.hacks.ray.Matrix;

public final class Intersection {
    /**
     * The distance along the ray (potentially negative) where
     * this intersectino happened
     */
    private final double t;

    /**
     * The point where this intersection takes place
     */
    private final Matrix point;

    /**
     * The normal of the surface where the intersection happened
     */
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

    public double getT() {
        return t;
    }

    public Matrix getPoint() {
        return point;
    }

    public Matrix getNormal() {
        return normal;
    }

    @Override
    public String toString() {
        return "Intersection.of(" + t + ", " + normal + ")";
    }
}
