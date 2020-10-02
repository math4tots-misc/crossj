package sanity.hacks.ray;

import crossj.Assert;
import crossj.M;
import crossj.Test;
import crossj.hacks.ray.math.Matrix;

/**
 * Chapter 4: Matrix Transformations
 */
public final class Chapter04 {

    @Test
    public static void translationsAndScaling() {
        {
            // Multiplying by a translation matrix
            Matrix transform = Matrix.translation(5, -3, 2);
            Matrix p = Matrix.point(-3, 4, 5);
            Assert.equals(transform.multiply(p), Matrix.point(2, 1, 7));
        }
        {
            // Multiplying by the inverse of a translation matrix
            Matrix transform = Matrix.translation(5, -3, 2);
            Matrix inv = transform.inverse();
            Matrix p = Matrix.point(-3, 4, 5);
            Assert.equals(inv.multiply(p), Matrix.point(-8, 7, 3));
        }
        {
            // Translation does not affect vectors
            Matrix transform = Matrix.translation(5, -3, 2);
            Matrix v = Matrix.vector(-3, 4, 5);
            Assert.equals(transform.multiply(v), v);
        }
        {
            // A scaling matrix applied to a point
            Matrix transform = Matrix.scaling(2, 3, 4);
            Matrix p = Matrix.point(-4, 6, 8);
            Assert.equals(transform.multiply(p), Matrix.point(-8, 18, 32));
        }
        {
            // A scaling matrix applied to a vector
            Matrix transform = Matrix.scaling(2, 3, 4);
            Matrix v = Matrix.vector(-4, 6, 8);
            Assert.equals(transform.multiply(v), Matrix.vector(-8, 18, 32));
        }
        {
            // Multiplying by the inverse of a scaling matrix
            Matrix transform = Matrix.scaling(2, 3, 4);
            Matrix inv = transform.inverse();
            Matrix v = Matrix.vector(-4, 6, 8);
            Assert.equals(inv.multiply(v), Matrix.vector(-2, 2, 2));
        }
        {
            // Reflection is scaling by a negative value
            Matrix transform = Matrix.scaling(-1, 1, 1);
            Matrix p = Matrix.point(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.point(-2, 3, 4));
        }
    }

    @Test
    public static void rotations() {
        {
            // Rotating a point around the x axis
            Matrix p = Matrix.point(0, 1, 0);
            Matrix halfQuarter = Matrix.xRotation(M.TAU / 8);
            Matrix fullQuarter = Matrix.xRotation(M.TAU / 4);
            Assert.almostEquals(halfQuarter.multiply(p), Matrix.point(0, M.sqrt(2) / 2, M.sqrt(2) / 2));
            Assert.almostEquals(fullQuarter.multiply(p), Matrix.point(0, 0, 1));
        }
        {
            // The inverse of an x-rotation rotates in the opposite direction
            Matrix p = Matrix.point(0, 1, 0);
            Matrix halfQuarter = Matrix.xRotation(M.TAU / 8);
            Matrix inv = halfQuarter.inverse();
            Assert.almostEquals(inv.multiply(p), Matrix.point(0, M.sqrt(2) / 2, -M.sqrt(2) / 2));
        }
        {
            // Rotating a point around the y axis
            Matrix p = Matrix.point(0, 0, 1);
            Matrix halfQuarter = Matrix.yRotation(M.TAU / 8);
            Matrix fullQuarter = Matrix.yRotation(M.TAU / 4);
            Assert.almostEquals(halfQuarter.multiply(p), Matrix.point(M.sqrt(2)/2, 0, M.sqrt(2)/2));
            Assert.almostEquals(fullQuarter.multiply(p), Matrix.point(1, 0, 0));
        }
        {
            // Rotating a point around the z axis
            Matrix p = Matrix.point(0, 1, 0);
            Matrix halfQuarter = Matrix.zRotation(M.TAU / 8);
            Matrix fullQuarter = Matrix.zRotation(M.TAU / 4);
            Assert.almostEquals(halfQuarter.multiply(p), Matrix.point(-M.sqrt(2)/2, M.sqrt(2)/2, 0));
            Assert.almostEquals(fullQuarter.multiply(p), Matrix.point(-1, 0, 0));
        }
    }

    @Test
    public static void shearing() {
        {
            // A shearing transformation moves x in proportion to y
            Matrix transform = Matrix.shearing(1, 0, 0, 0, 0, 0);
            Matrix p = Matrix.point(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.point(5, 3, 4));
        }
        {
            // A shearing transformation moves x in proportion to z
            Matrix transform = Matrix.shearing(0, 1, 0, 0, 0, 0);
            Matrix p = Matrix.point(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.point(6, 3, 4));
        }
        {
            // A shearing transformation moves y in proportion to x
            Matrix transform = Matrix.shearing(0, 0, 1, 0, 0, 0);
            Matrix p = Matrix.point(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.point(2, 5, 4));
        }
        {
            // A shearing transformation moves y in proportion to z
            Matrix transform = Matrix.shearing(0, 0, 0, 1, 0, 0);
            Matrix p = Matrix.point(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.point(2, 7, 4));
        }
        {
            // A shearing transformation moves z in proportion to x
            Matrix transform = Matrix.shearing(0, 0, 0, 0, 1, 0);
            Matrix p = Matrix.point(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.point(2, 3, 6));
        }
        {
            // A shearing transformation moves z in proportion to y
            Matrix transform = Matrix.shearing(0, 0, 0, 0, 0, 1);
            Matrix p = Matrix.point(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.point(2, 3, 7));
        }
    }

    @Test
    public static void chaining() {
        {
            // Individual transformations are applied in sequence
            Matrix p = Matrix.point(1, 0, 1);
            Matrix a = Matrix.xRotation(M.TAU / 4);
            Matrix b = Matrix.scaling(5, 5, 5);
            Matrix c = Matrix.translation(10, 5, 7);
            Matrix p2 = a.multiply(p);
            Assert.almostEquals(p2, Matrix.point(1, -1, 0));
            Matrix p3 = b.multiply(p2);
            Assert.almostEquals(p3, Matrix.point(5, -5, 0));
            Matrix p4 = c.multiply(p3);
            Assert.equals(p4, Matrix.point(15, 0, 7));
        }
        {
            // Chained transformations must be applied in reverse order
            Matrix p = Matrix.point(1, 0, 1);
            Matrix a = Matrix.xRotation(M.TAU / 4);
            Matrix b = Matrix.scaling(5, 5, 5);
            Matrix c = Matrix.translation(10, 5, 7);
            Matrix t = c.multiply(b).multiply(a);
            Assert.equals(t.multiply(p), Matrix.point(15, 0, 7));
        }
    }
}
