package sanity.hacks.ray3;

import crossj.Assert;
import crossj.Test;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.AABB;
import crossj.hacks.ray3.geo.Ray;

/**
 * Tests for axis aligned bounding boxes
 */
public final class AABBTest {
    @Test
    public static void hit() {
        {
            var box = AABB.withPoints(Matrix.point(0, 0, 0), Matrix.point(1, 1, 1));

            // simple ray smack through the middle of one of its faces
            Assert.that(box.hit(Ray.of(Matrix.point(0.5, 0.5, -1), Matrix.vector(0, 0, 1))));

            // ray hitting from the inside
            Assert.that(box.hit(Ray.of(Matrix.point(0.5, 0.5, 0.5), Matrix.vector(0, 0, 1))));

            // miss
            Assert.that(!box.hit(Ray.of(Matrix.point(0.5, 0.5, 5), Matrix.vector(0, 0, 1))));

            // hit from inside at a corner
            Assert.that(box.hit(Ray.of(Matrix.point(0.5, 0.5, 0.5), Matrix.vector(1, 1, 1))));
        }
    }
}
