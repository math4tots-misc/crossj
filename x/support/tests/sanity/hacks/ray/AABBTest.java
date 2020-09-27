package sanity.hacks.ray;

import crossj.Assert;
import crossj.Test;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.geo.AABB;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Sphere;

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

    @Test
    public static void forSphere() {
        {
            var s = Sphere.withTransform(Matrix.scaling(2, 2, 2).thenTranslate(10, 10, 10));
            var box = s.getBoundingBox();
            var ray = Ray.of(Matrix.point(10, 10, 7), Matrix.vector(0, 0, 1));
            Assert.that(box.hit(ray));
        }
    }
}
