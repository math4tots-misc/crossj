package crossj.hacks.ray.geo;

import crossj.base.Assert;
import crossj.hacks.ray.math.Matrix;


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

    public Ray transform(Matrix t) {
        return Ray.of(t.multiply(origin), t.multiply(direction));
    }

    @Override
    public String toString() {
        return "Ray.of(" + origin + ", " + direction + ")";
    }
}
