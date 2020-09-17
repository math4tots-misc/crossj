package sanity.hacks.ray;

import crossj.Assert;
import crossj.M;
import crossj.Test;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray.geo.Sphere;

/**
 * Chapter 6: Light and Shading
 */
public final class Chapter06 {
    @Test
    public static void normals() {
        {
            // The normal on a sphere at a point on the x axis
            var s = Sphere.unit();
            var n = s.normalAt(Matrix.point(1, 0, 0));
            Assert.equals(n, Matrix.vector(1, 0, 0));
        }
        {
            // The normal on a sphere at a point on the y axis
            var s = Sphere.unit();
            var n = s.normalAt(Matrix.point(0, 1, 0));
            Assert.equals(n, Matrix.vector(0, 1, 0));
        }
        {
            // The normal on a sphere at a point on the z axis
            var s = Sphere.unit();
            var n = s.normalAt(Matrix.point(0, 0, 1));
            Assert.equals(n, Matrix.vector(0, 0, 1));
        }
        {
            // The normal on a sphere at a nonaxsial point
            var s = Sphere.unit();
            var n = s.normalAt(Matrix.point(M.sqrt(3)/3, M.sqrt(3)/3, M.sqrt(3)/3));
            Assert.equals(n, Matrix.vector(M.sqrt(3)/3, M.sqrt(3)/3, M.sqrt(3)/3));
        }
        {
            // The normal is a normalized vector
            var s = Sphere.unit();
            var n = s.normalAt(Matrix.point(M.sqrt(3)/3, M.sqrt(3)/3, M.sqrt(3)/3));
            Assert.equals(n, n.normalize());
        }
        {
            // Computing the normal on a translated sphere
            var s = Sphere.unit();
            s.setTransform(Matrix.translation(0, 1, 0));
            var n = s.normalAt(Matrix.point(0, 1.70711, -0.70711));
            Assert.less(n.subtract(Matrix.vector(0, 0.70711, -0.70711)).magnitude(), 0.0001);
        }
        {
            // Computing the normal on a transformed sphere
            var s = Sphere.unit();
            var m = Matrix.scaling(1, 0.5, 1).multiply(Matrix.zRotation(M.TAU/10));
            s.setTransform(m);
            var n = s.normalAt(Matrix.point(0, M.sqrt(2)/2, -M.sqrt(2)/2));
            n.toString();
        }
    }
}
