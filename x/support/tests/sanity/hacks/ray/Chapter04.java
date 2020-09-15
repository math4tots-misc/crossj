package sanity.hacks.ray;

import crossj.Assert;
import crossj.Test;
import crossj.hacks.ray.Matrix;

/**
 * Chapter 4: Matrix Transformations
 */
public final class Chapter04 {

    @Test
    public static void translations() {
        {
            // Multiplying by a translation matrix
            Matrix transform = Matrix.translation(5, -3, 2);
            Matrix p = Matrix.newPoint(-3, 4, 5);
            Assert.equals(transform.multiply(p), Matrix.newPoint(2, 1, 7));
        }
        {
            // Multiplying by the inverse of a translation matrix
            Matrix transform = Matrix.translation(5, -3, 2);
            Matrix inv = transform.inverse();
            Matrix p = Matrix.newPoint(-3, 4, 5);
            Assert.equals(inv.multiply(p), Matrix.newPoint(-8, 7, 3));
        }
        {
            // Translation does not affect vectors
            Matrix transform = Matrix.translation(5, -3, 2);
            Matrix v = Matrix.newVector(-3, 4, 5);
            Assert.equals(transform.multiply(v), v);
        }
        {
            // A scaling matrix applied to a point
            Matrix transform = Matrix.scaling(2, 3, 4);
            Matrix p = Matrix.newPoint(-4, 6, 8);
            Assert.equals(transform.multiply(p), Matrix.newPoint(-8, 18, 32));
        }
        {
            // A scaling matrix applied to a vector
            Matrix transform = Matrix.scaling(2, 3, 4);
            Matrix v = Matrix.newVector(-4, 6, 8);
            Assert.equals(transform.multiply(v), Matrix.newVector(-8, 18, 32));
        }
        {
            // Multiplying by the inverse of a scaling matrix
            Matrix transform = Matrix.scaling(2, 3, 4);
            Matrix inv = transform.inverse();
            Matrix v = Matrix.newVector(-4, 6, 8);
            Assert.equals(inv.multiply(v), Matrix.newVector(-2, 2, 2));
        }
        {
            // Reflection is scaling by a negative value
            Matrix transform = Matrix.scaling(-1, 1, 1);
            Matrix p = Matrix.newPoint(2, 3, 4);
            Assert.equals(transform.multiply(p), Matrix.newPoint(-2, 3, 4));
        }
    }
}
