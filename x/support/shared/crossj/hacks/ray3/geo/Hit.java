package crossj.hacks.ray3.geo;

import crossj.hacks.ray.Matrix;

public final class Hit {
    private final double t;
    private final Matrix point;
    private final Matrix normal;

    private Hit(double t, Matrix point, Matrix normal) {
        this.t = t;
        this.point = point;
        this.normal = normal;
    }

    public static Hit of(double t, Matrix point, Matrix normal) {
        return new Hit(t, point, normal);
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
}
