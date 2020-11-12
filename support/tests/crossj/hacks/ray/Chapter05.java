package crossj.hacks.ray;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.hacks.ray.geo.DeprecatedIntersection;
import crossj.hacks.ray.geo.DeprecatedIntersections;
import crossj.hacks.ray.geo.DeprecatedRay;
import crossj.hacks.ray.geo.DeprecatedSphere;
import crossj.hacks.ray.math.Matrix;

/**
 * Chapter 5: Ray-Sphere Intersections
 */
public final class Chapter05 {

    @Test
    public static void rays() {
        {
            // Creating and querying a ray
            Matrix origin = Matrix.point(1, 2, 3);
            Matrix direction = Matrix.vector(4, 5, 6);
            DeprecatedRay ray = DeprecatedRay.of(origin, direction);
            Assert.equals(ray.getOrigin(), origin);
            Assert.equals(ray.getDirection(), direction);
        }
        {
            // Computing a point from a distance
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(2, 3, 4), Matrix.vector(1, 0, 0));
            Assert.equals(r.position(0), Matrix.point(2, 3, 4));
            Assert.equals(r.position(1), Matrix.point(3, 3, 4));
            Assert.equals(r.position(-1), Matrix.point(1, 3, 4));
            Assert.equals(r.position(2.5), Matrix.point(4.5, 3, 4));
        }
    }

    @Test
    public static void spheres() {
        {
            // A ray intersects a sphere at two points
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 0, -5), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), 4.0);
            Assert.equals(xs.get(1).getT(), 6.0);
        }
        {
            // A ray intersects a sphere at a tangent
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 1, -5), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), 5.0);
            Assert.equals(xs.get(1).getT(), 5.0);
        }
        {
            // A ray misses a sphere
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 2, -5), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 0);
        }
        {
            // A ray originates inside a sphere
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 0, 0), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), -1.0);
            Assert.equals(xs.get(1).getT(), 1.0);
        }
        {
            // A sphere is behind a ray
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 0, 5), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), -6.0);
            Assert.equals(xs.get(1).getT(), -4.0);
        }
        {
            // An intersection encapsulates t and object
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersection i = DeprecatedIntersection.of(3.5, s);
            Assert.equals(i.getT(), 3.5);
            Assert.equals(i.getObject(), s);
        }
        {
            // Aggregating intersections
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersection i1 = DeprecatedIntersection.of(1, s);
            DeprecatedIntersection i2 = DeprecatedIntersection.of(2, s);
            DeprecatedIntersections xs = DeprecatedIntersections.of(i1, i2);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), 1.0);
            Assert.equals(xs.get(1).getT(), 2.0);
        }
        {
            // Intersect sets the object on the intersection
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 0, -5), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getObject(), s);
            Assert.equals(xs.get(1).getObject(), s);
        }
        {
            // The hit, when all intersections have positive t
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersection i1 = DeprecatedIntersection.of(1, s);
            DeprecatedIntersection i2 = DeprecatedIntersection.of(2, s);
            DeprecatedIntersections xs = DeprecatedIntersections.of(i2, i1);
            DeprecatedIntersection i = xs.getHit().get();
            Assert.equals(i, i1);
        }
        {
            // The hit, when some intersections have negative t
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersection i1 = DeprecatedIntersection.of(-1, s);
            DeprecatedIntersection i2 = DeprecatedIntersection.of(1, s);
            DeprecatedIntersections xs = DeprecatedIntersections.of(i2, i1);
            DeprecatedIntersection i = xs.getHit().get();
            Assert.equals(i, i2);
        }
        {
            // The hit, when all intersections have negative t
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersection i1 = DeprecatedIntersection.of(-2, s);
            DeprecatedIntersection i2 = DeprecatedIntersection.of(-1, s);
            DeprecatedIntersections xs = DeprecatedIntersections.of(i2, i1);
            Assert.that(xs.getHit().isEmpty());
        }
        {
            // The hit is always the lowest nonnegative intersection
            DeprecatedSphere s = DeprecatedSphere.unit();
            DeprecatedIntersection i1 = DeprecatedIntersection.of(5, s);
            DeprecatedIntersection i2 = DeprecatedIntersection.of(7, s);
            DeprecatedIntersection i3 = DeprecatedIntersection.of(-3, s);
            DeprecatedIntersection i4 = DeprecatedIntersection.of(2, s);
            DeprecatedIntersections xs = DeprecatedIntersections.of(i1, i2, i3, i4);
            Assert.equals(xs.getHit().get(), i4);
        }
    }

    @Test
    public static void transform() {
        {
            // Translating a ray
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(1, 2, 3), Matrix.vector(0, 1, 0));
            Matrix m = Matrix.translation(3, 4, 5);
            DeprecatedRay r2 = r.transform(m);
            Assert.equals(r2.getOrigin(), Matrix.point(4, 6, 8));
            Assert.equals(r2.getDirection(), Matrix.vector(0, 1, 0));
        }
        {
            // Scaling a ray
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(1, 2, 3), Matrix.vector(0, 1, 0));
            Matrix m = Matrix.scaling(2, 3, 4);
            DeprecatedRay r2 = r.transform(m);
            Assert.equals(r2.getOrigin(), Matrix.point(2, 6, 12));
            Assert.equals(r2.getDirection(), Matrix.vector(0, 3, 0));
        }
        {
            // A sphere's default transformation
            DeprecatedSphere s = DeprecatedSphere.unit();
            Assert.equals(s.getTransform(), Matrix.identity(4));
        }
        {
            // Changing a sphere's transformation
            DeprecatedSphere s = DeprecatedSphere.unit();
            Matrix t = Matrix.translation(2, 3, 4);
            s.setTransform(t);
            Assert.equals(s.getTransform(), t);
        }
        {
            // Intersecting a scaled sphere with a ray
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 0, -5), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            s.setTransform(Matrix.scaling(2, 2, 2));
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), 3.0);
            Assert.equals(xs.get(1).getT(), 7.0);
        }
        {
            // Intersecting a translated sphere with a ray
            DeprecatedRay r = DeprecatedRay.of(Matrix.point(0, 0, -5), Matrix.vector(0, 0, 1));
            DeprecatedSphere s = DeprecatedSphere.unit();
            s.setTransform(Matrix.translation(5, 0, 0));
            DeprecatedIntersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 0);
        }
    }
}
