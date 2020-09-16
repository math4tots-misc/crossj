package crossj.hacks.ray.geo;

import crossj.Assert;
import crossj.hacks.ray.Matrix;

/**
 * First described in Chapter 5 (Ray-Sphere intersections)
 */
public final class Ray {
    private final Matrix origin;
    private final Matrix direction;

    private Ray(Matrix origin, Matrix direction) {
        Assert.withMessage(origin.isPoint(), "A ray's origin must be a point");
        Assert.withMessage(direction.isVector(), "A ray's direction must be a vector");
        this.origin = origin;
        this.direction = direction;
    }

    public static Ray of(Matrix origin, Matrix direction) {
        return new Ray(origin, direction);
    }

    public Matrix getOrigin() {
        return origin;
    }

    public Matrix getDirection() {
        return direction;
    }

    public Matrix position(double t) {
        return origin.add(direction.scale(t));
    }
}