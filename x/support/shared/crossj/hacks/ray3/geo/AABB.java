package crossj.hacks.ray3.geo;

import crossj.Assert;
import crossj.IO;
import crossj.M;
import crossj.hacks.ray.Matrix;

/**
 * Axis Aligned Bounding Box
 */
public final class AABB {
    private final Matrix min;
    private final Matrix max;

    private AABB(Matrix min, Matrix max) {
        this.min = min;
        this.max = max;
    }

    public static AABB withCoordinates(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AABB(Matrix.point(M.min(x1, x2), M.min(y1, y2), M.min(z1, z2)),
                Matrix.point(M.max(x1, x2), M.max(y1, y2), M.max(z1, z2)));
    }

    public static AABB withPoints(Matrix p1, Matrix p2) {
        Assert.withMessage(p1.isPoint(), "AABB.withPoints p1 arg must be a point");
        Assert.withMessage(p2.isPoint(), "AABB.withPoints p2 arg must be a point");
        return withCoordinates(p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ());
    }

    /**
     * Alias for <code>this.hitInRange(ray, Surface.DEFAULT_T_MIN, Surface.DEFAULT_T_MAX)</code>
     */
    public boolean hit(Ray ray) {
        return hitInRange(ray, Surface.DEFAULT_T_MIN, Surface.DEFAULT_T_MAX);
    }

    /**
     * Test if the given ray hits this box.
     *
     * Code more or less based on section 3.5 of
     * https://raytracing.github.io/books/RayTracingTheNextWeek.html
     */
    public boolean hitInRange(Ray ray, double tmin, double tmax) {
        for (int a = 0; a < 3; a++) {
            var invD = 1.0 / ray.getDirection().get(a, 0);
            var t0 = (min.get(a, 0) - ray.getOrigin().get(a, 0)) * invD;
            var t1 = (max.get(a, 0) - ray.getOrigin().get(a, 0)) * invD;
            if (invD < 0) {
                var tmp = t0;
                t0 = t1;
                t1 = tmp;
            }
            tmin = t0 > tmin ? t0 : tmin;
            tmax = t1 < tmax ? t1 : tmax;
            if (tmax <= tmin) {
                return false;
            }
        }
        return true;
    }
}
