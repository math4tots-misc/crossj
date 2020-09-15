package sanity.hacks.ray;

import crossj.Assert;
import crossj.M;
import crossj.Test;
import crossj.hacks.ray.Matrix;

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
}
