package sanity.hacks.ray3;

import crossj.Assert;
import crossj.Test;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Ray;
import crossj.hacks.ray3.geo.Triangle;

public final class TriangleTest {
    @Test
    public static void hit() {
        var t = Triangle.at(Matrix.point(0, 0, 0), Matrix.point(2, 0, 0), Matrix.point(1, 2, 0));

        // hit somewhere in the middle
        Assert.that(t.hitRay(Ray.of(Matrix.point(1, 1, -2), Matrix.vector(0, 0, 1))).isPresent());

        // facing the opposite direction
        Assert.that(!t.hitRay(Ray.of(Matrix.point(1, 1, -2), Matrix.vector(0, 0, -1))).isPresent());

        // hits the plane, but misses triangle
        Assert.that(!t.hitRay(Ray.of(Matrix.point(10, 1, -2), Matrix.vector(0, 0, 1))).isPresent());

        // hits the bounding box, but misses triangle
        Assert.that(!t.hitRay(Ray.of(Matrix.point(1.9, 1.9, -2), Matrix.vector(0, 0, 1))).isPresent());

        // parallel to plane
        Assert.that(!t.hitRay(Ray.of(Matrix.point(10, 1, -2), Matrix.vector(0, 1, 0))).isPresent());
    }
}
