package crossj.hacks.ray3.geo;

import crossj.Assert;
import crossj.List;
import crossj.M;
import crossj.Tuple;
import crossj.XIterable;
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

    public static AABB unbounded() {
        var min = -M.INFINITY;
        var max = M.INFINITY;
        return withCoordinates(min, min, min, max, max, max);
    }

    public static AABB withCoordinates(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AABB(Matrix.point(M.min(x1, x2), M.min(y1, y2), M.min(z1, z2)),
                Matrix.point(M.max(x1, x2), M.max(y1, y2), M.max(z1, z2)));
    }

    public static AABB withPoints(Matrix... pts) {
        return fromPoints(Tuple.fromJavaArray(pts));
    }

    public Matrix getMin() {
        return min;
    }

    public Matrix getMax() {
        return max;
    }

    public Matrix getCenter() {
        return min.add(max).scale(0.5);
    }

    /**
     * Create a new AABB that surrounds all the given AABBs.
     */
    public static AABB join(XIterable<AABB> aabbs) {
        return fromPoints(aabbs.iter().flatMap(aabb -> List.of(aabb.min, aabb.max)));
    }

    /**
     * Returns the smallest axis aligned bounding box containing all the given
     * points
     */
    public static AABB fromPoints(XIterable<Matrix> points) {
        var pts = Tuple.fromIterable(points);
        var xmin = pts.get(0).getX();
        var xmax = xmin;
        var ymin = pts.get(0).getY();
        var ymax = ymin;
        var zmin = pts.get(0).getZ();
        var zmax = zmin;
        for (var point : pts) {
            Assert.withMessage(point.isPoint(), "AABB.fromPoints expects every entry in points to be a Point");
            xmin = M.min(xmin, point.getX());
            xmax = M.max(xmax, point.getX());
            ymin = M.min(ymin, point.getY());
            ymax = M.max(ymax, point.getY());
            zmin = M.min(zmin, point.getZ());
            zmax = M.max(zmax, point.getZ());
        }
        return withCoordinates(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    public double getMinForAxis(int axis) {
        return min.get(axis, 0);
    }

    public double getMaxForAxis(int axis) {
        return max.get(axis, 0);
    }

    public double getMidpointForAxis(int axis) {
        return (getMinForAxis(axis) + getMaxForAxis(axis)) / 2;
    }

    /**
     * Alias for
     * <code>this.hitInRange(ray, Surface.DEFAULT_T_MIN, Surface.DEFAULT_T_MAX)</code>
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

    @Override
    public String toString() {
        return "AABB.withPoints(" + min + ", " + max + ")";
    }
}
