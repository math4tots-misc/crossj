package crossj.hacks.ray;

import crossj.base.Assert;
import crossj.base.DoubleArray;
import crossj.base.List;
import crossj.base.Test;
import crossj.hacks.ray.math.Matrix;

/**
 * Chapter 3: Matrices
 */
public final class Chapter03 {

    @Test
    public static void creatingMatrices() {
        {
            // Constructing and inspecting a 4x4 matrix
            Matrix m = Matrix.withRows(
                List.ofDoubles(1, 2, 3, 4),
                List.ofDoubles(5.5, 6.5, 7.5, 8.5),
                List.ofDoubles(9, 10, 11, 12),
                List.ofDoubles(13.5, 14.5, 15.5, 16.5)
            );
            Assert.equals(m.get(0, 0), 1.0);
            Assert.equals(m.get(0, 3), 4.0);
            Assert.equals(m.get(1, 0), 5.5);
            Assert.equals(m.get(1, 2), 7.5);
            Assert.equals(m.get(2, 2), 11.0);
            Assert.equals(m.get(3, 0), 13.5);
            Assert.equals(m.get(3, 2), 15.5);
        }
        {
            // A 2x2 matrix ought to be representable
            Matrix m = Matrix.withRows(
                DoubleArray.of(-3, 5),
                DoubleArray.of(1, -2)
            );
            Assert.equals(m.get(0, 0), -3.0);
            Assert.equals(m.get(0, 1), 5.0);
            Assert.equals(m.get(1, 0), 1.0);
            Assert.equals(m.get(1, 1), -2.0);
        }
        {
            // A 3x3 matrix ought to be representable
            Matrix m = Matrix.withRows(
                List.ofDoubles(-3, 5, 0),
                List.ofDoubles(1, -2, -7),
                List.ofDoubles(0, 1, 1)
            );
            Assert.equals(m.get(0, 0), -3.0);
            Assert.equals(m.get(1, 1), -2.0);
            Assert.equals(m.get(2, 2), 1.0);
        }
        {
            // Matrix equality with identical matrices
            Matrix a = Matrix.withRows(
                List.ofDoubles(1, 2, 3, 4),
                List.ofDoubles(5, 6, 7, 8),
                List.ofDoubles(9, 8, 7, 6),
                List.ofDoubles(5, 4, 3, 2)
            );
            Matrix b = Matrix.withRows(
                List.ofDoubles(1, 2, 3, 4),
                List.ofDoubles(5, 6, 7, 8),
                List.ofDoubles(9, 8, 7, 6),
                List.ofDoubles(5, 4, 3, 2)
            );
            Assert.equals(a, b);
        }
        {
            // Matrix equality with different matrices
            Matrix a = Matrix.withRows(
                List.ofDoubles(1, 2, 3, 4),
                List.ofDoubles(5, 6, 7, 8),
                List.ofDoubles(9, 8, 7, 6),
                List.ofDoubles(5, 4, 3, 2)
            );
            Matrix b = Matrix.withRows(
                List.ofDoubles(2, 3, 4, 5),
                List.ofDoubles(6, 7, 8, 9),
                List.ofDoubles(8, 7, 6, 5),
                List.ofDoubles(4, 3, 2, 1)
            );
            Assert.notEquals(a, b);
        }
    }

    @Test
    public static void multiplyingMatrices() {
        {
            // Multiplying two matrices
            Matrix a = Matrix.withRows(
                List.ofDoubles(1, 2, 3, 4),
                List.ofDoubles(5, 6, 7, 8),
                List.ofDoubles(9, 8, 7, 6),
                List.ofDoubles(5, 4, 3, 2)
            );
            Matrix b = Matrix.withRows(
                List.ofDoubles(-2, 1, 2, 3),
                List.ofDoubles(3, 2, 1, -1),
                List.ofDoubles(4, 3, 6, 5),
                List.ofDoubles(1, 2, 7, 8)
            );
            Assert.equals(a.multiply(b), Matrix.withRows(
                List.ofDoubles(20, 22, 50, 48),
                List.ofDoubles(44, 54, 114, 108),
                List.ofDoubles(40, 58, 110, 102),
                List.ofDoubles(16, 26, 46, 42)
            ));
        }
        {
            // A matrix multiplied by a tuple
            Matrix a = Matrix.withRows(
                List.ofDoubles(1, 2, 3, 4),
                List.ofDoubles(2, 4, 4, 2),
                List.ofDoubles(8, 6, 4, 1),
                List.ofDoubles(0, 0, 0, 1)
            );
            Matrix b = Matrix.tuple(1, 2, 3, 1);
            Assert.equals(a.multiply(b), Matrix.tuple(18, 24, 33, 1));
        }
        {
            // Multiplying a matrix by the identity matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles(0, 1, 2, 4),
                List.ofDoubles(1, 2, 4, 8),
                List.ofDoubles(2, 4, 8, 16),
                List.ofDoubles(4, 8, 16, 32)
            );
            Assert.equals(a.multiply(Matrix.identity(4)), a);
        }
        {
            // Multiplying the identity matrix by a tuple
            Matrix a = Matrix.tuple(1, 2, 3, 4);
            Assert.equals(Matrix.identity(4).multiply(a), a);
        }
    }

    @Test
    public static void transposingMatrices() {
        {
            // Transposing a matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles(0, 9, 3, 0),
                List.ofDoubles(9, 8, 0, 8),
                List.ofDoubles(1, 8, 5, 3),
                List.ofDoubles(0, 0, 5, 8)
            );
            Assert.equals(a.transpose(), Matrix.withRows(
                List.ofDoubles(0, 9, 1, 0),
                List.ofDoubles(9, 8, 8, 0),
                List.ofDoubles(3, 0, 5, 5),
                List.ofDoubles(0, 8, 3, 8)
            ));
        }
        {
            // Transposing the identity matrix
            Matrix a = Matrix.identity(4);
            Assert.equals(a.transpose(), a);
        }
    }

    @Test
    public static void invertingMatrices() {
        {
            // Calculating the determinant of a 2x2 matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles(1, 5),
                List.ofDoubles(-3, 2)
            );
            Assert.equals(a.determinant(), 17.0);
        }
        // ------------------------------------------
        // skip a few tests related to submatrices...
        // ------------------------------------------
        {
            // Calculating the determinant of a 3x3 matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles(1, 2, 6),
                List.ofDoubles(-5, 8, -4),
                List.ofDoubles(2, 6, 4)
            );
            Assert.almostEquals(a.determinant(), -196);
        }
        {
            // Calculating the determinant of a 4x4 matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles(-2, -8, 3, 5),
                List.ofDoubles(-3, 1, 7, 3),
                List.ofDoubles(1, 2, -9, 6),
                List.ofDoubles(-6, 7, 7, -9)
            );
            Assert.equals(a.determinant(), -4071.0);
        }
        {
            // Testing an invertible matrix for invertibility
            Matrix a = Matrix.withRows(
                List.ofDoubles(6, 4, 4, 4),
                List.ofDoubles(5, 5, 7, 6),
                List.ofDoubles(4, -9, 3, -7),
                List.ofDoubles(9, 1, 7, -6)
            );
            Assert.almostEquals(a.determinant(), -2120.0);
            Assert.that(a.isInvertible());
        }
        {
            // Testing a noninvertible matrix for invertibility
            Matrix a = Matrix.withRows(
                List.ofDoubles(-4, 2, -2, -3),
                List.ofDoubles(9, 6, 2, 6),
                List.ofDoubles(0, -5, 1, -5),
                List.ofDoubles(0, 0, 0, 0)
            );
            Assert.almostEquals(a.determinant(), 0.0);
            Assert.that(!a.isInvertible());
        }
        {
            // Calculating the inverse of a matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles(-5, 2, 6, -8),
                List.ofDoubles(1, -5, 1, 8),
                List.ofDoubles(7, 7, -6, -7),
                List.ofDoubles(1, -3, 7, 4)
            );
            Matrix b = a.inverse();
            Assert.almostEquals(a.determinant(), 532.0);
            Assert.almostEquals(b.get(3, 2), -160.0/532);
            Assert.equals(b.get(2, 3), 105.0/532);

            // For 'b', the test data itself doesn't have a lot of precision.
            // Instead of lowering the precision for all uses of 'almostEquals',
            // in these cases I'm optiming to manually check that the result
            // isn't "too far" from the expected value using a drift value appropriate
            // for given the context.
            Matrix expectedB = Matrix.withRows(
                List.ofDoubles( 0.21805,  0.45113,  0.24060, -0.04511),
                List.ofDoubles(-0.80827, -1.45677, -0.44361,  0.52068),
                List.ofDoubles(-0.07895, -0.22368, -0.05263,  0.19737),
                List.ofDoubles(-0.52256, -0.81391, -0.30075,  0.30639)
            );
            Assert.less(b.subtract(expectedB).magnitude(), 0.0001);
        }
        {
            // Calculating the inverse of another matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles(8, -5, 9, 2),
                List.ofDoubles(7, 5, 6, 1),
                List.ofDoubles(-6, 0, 9, 6),
                List.ofDoubles(-3, 0, -9, -4)
            );
            Matrix expected = Matrix.withRows(
                List.ofDoubles(-0.15385, -0.15385, -0.28205, -0.53846),
                List.ofDoubles(-0.07692,  0.12308,  0.02564,  0.03077),
                List.ofDoubles( 0.35897,  0.35897,  0.43590,  0.92308),
                List.ofDoubles(-0.69231, -0.69231, -0.76923, -1.92308)
            );
            Assert.less(a.inverse().subtract(expected).magnitude(), 0.0001);
        }
        {
            // Calculating the inverse of a third matrix
            Matrix a = Matrix.withRows(
                List.ofDoubles( 9,  3,  0,  9),
                List.ofDoubles(-5, -2, -6, -3),
                List.ofDoubles(-4,  9,  6,  4),
                List.ofDoubles(-7,  6,  6,  2)
            );
            Matrix expected = Matrix.withRows(
                List.ofDoubles(-0.04074, -0.07778,  0.14444, -0.22222),
                List.ofDoubles(-0.07778,  0.03333,  0.36667, -0.33333),
                List.ofDoubles(-0.02901, -0.14630, -0.10926,  0.12963),
                List.ofDoubles( 0.17778,  0.06667, -0.26667,  0.33333)
            );
            Assert.less(a.inverse().subtract(expected).magnitude(), 0.0001);
        }
        {
            // Multiplying a product by its inverse
            Matrix a = Matrix.withRows(
                List.ofDoubles( 3, -9,  7, 3),
                List.ofDoubles( 3, -8,  2, -9),
                List.ofDoubles(-4,  4,  4,  1),
                List.ofDoubles(-6,  5, -1,  1)
            );
            Matrix b = Matrix.withRows(
                List.ofDoubles( 8,  2,  2,  2),
                List.ofDoubles( 3, -1,  7,  0),
                List.ofDoubles( 7,  0,  5,  4),
                List.ofDoubles( 6, -2,  0,  5)
            );
            Matrix c = a.multiply(b);
            Assert.almostEquals(c.multiply(b.inverse()), a);
        }
    }
}
