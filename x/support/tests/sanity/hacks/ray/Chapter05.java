package sanity.hacks.ray;

import crossj.Assert;
import crossj.Test;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.geo.Intersection;
import crossj.hacks.ray.geo.Intersections;
import crossj.hacks.ray.geo.Ray;
import crossj.hacks.ray.geo.Sphere;

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
            Ray ray = Ray.of(origin, direction);
            Assert.equals(ray.getOrigin(), origin);
            Assert.equals(ray.getDirection(), direction);
        }
        {
            // Computing a point from a distance
            Ray r = Ray.of(Matrix.point(2, 3, 4), Matrix.vector(1, 0, 0));
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
            Ray r = Ray.of(Matrix.point(0, 0, -5), Matrix.vector(0, 0, 1));
            Sphere s = Sphere.unit();
            Intersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), 4.0);
            Assert.equals(xs.get(1).getT(), 6.0);
        }
        {
            // A ray intersects a sphere at a tangent
            Ray r = Ray.of(Matrix.point(0, 1, -5), Matrix.vector(0, 0, 1));
            Sphere s = Sphere.unit();
            Intersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), 5.0);
            Assert.equals(xs.get(1).getT(), 5.0);
        }
        {
            // A ray misses a sphere
            Ray r = Ray.of(Matrix.point(0, 2, -5), Matrix.vector(0, 0, 1));
            Sphere s = Sphere.unit();
            Intersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 0);
        }
        {
            // A ray originates inside a sphere
            Ray r = Ray.of(Matrix.point(0, 0, 0), Matrix.vector(0, 0, 1));
            Sphere s = Sphere.unit();
            Intersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), -1.0);
            Assert.equals(xs.get(1).getT(), 1.0);
        }
        {
            // A sphere is behind a ray
            Ray r = Ray.of(Matrix.point(0, 0, 5), Matrix.vector(0, 0, 1));
            Sphere s = Sphere.unit();
            Intersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), -6.0);
            Assert.equals(xs.get(1).getT(), -4.0);
        }
        {
            // An intersection encapsulates t and object
            Sphere s = Sphere.unit();
            Intersection i = Intersection.of(3.5, s);
            Assert.equals(i.getT(), 3.5);
            Assert.equals(i.getObject(), s);
        }
        {
            // Aggregating intersections
            Sphere s = Sphere.unit();
            Intersection i1 = Intersection.of(1, s);
            Intersection i2 = Intersection.of(2, s);
            Intersections xs = Intersections.of(i1, i2);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getT(), 1.0);
            Assert.equals(xs.get(1).getT(), 2.0);
        }
        {
            // Intersect sets the object on the intersection
            Ray r = Ray.of(Matrix.point(0, 0, -5), Matrix.vector(0, 0, 1));
            Sphere s = Sphere.unit();
            Intersections xs = s.intersectRay(r);
            Assert.equals(xs.size(), 2);
            Assert.equals(xs.get(0).getObject(), s);
            Assert.equals(xs.get(1).getObject(), s);
        }
        {
            // The hit, when all intersections have positive t
            Sphere s = Sphere.unit();
            Intersection i1 = Intersection.of(1, s);
            Intersection i2 = Intersection.of(2, s);
            Intersections xs = Intersections.of(i2, i1);
            Intersection i = xs.getHit().get();
            Assert.equals(i, i1);
        }
        {
            // The hit, when some intersections have negative t
            Sphere s = Sphere.unit();
            Intersection i1 = Intersection.of(-1, s);
            Intersection i2 = Intersection.of(1, s);
            Intersections xs = Intersections.of(i2, i1);
            Intersection i = xs.getHit().get();
            Assert.equals(i, i2);
        }
        {
            // The hit, when all intersections have negative t
            Sphere s = Sphere.unit();
            Intersection i1 = Intersection.of(-2, s);
            Intersection i2 = Intersection.of(-1, s);
            Intersections xs = Intersections.of(i2, i1);
            Assert.that(xs.getHit().isEmpty());
        }
        {
            // The hit is always the lowest nonnegative intersection
            Sphere s = Sphere.unit();
            Intersection i1 = Intersection.of(5, s);
            Intersection i2 = Intersection.of(7, s);
            Intersection i3 = Intersection.of(-3, s);
            Intersection i4 = Intersection.of(2, s);
            Intersections xs = Intersections.of(i1, i2, i3, i4);
            Assert.equals(xs.getHit().get(), i4);
        }
    }
}
