package sanity.hacks.ray;

import crossj.Assert;
import crossj.DoubleArray;
import crossj.List;
import crossj.Test;
import crossj.hacks.ray.Matrix;

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
            Matrix b = Matrix.newTuple(1, 2, 3, 1);
            Assert.equals(a.multiply(b), Matrix.newTuple(18, 24, 33, 1));
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
            Matrix a = Matrix.newTuple(1, 2, 3, 4);
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
    }
}
