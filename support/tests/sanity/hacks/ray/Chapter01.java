package sanity.hacks.ray;

import crossj.base.Assert;
import crossj.base.M;
import crossj.base.Test;
import crossj.hacks.ray.math.Matrix;

/**
 * Chapter 1: Tuples, Points, and Vectors
 */
public final class Chapter01 {
    @Test
    public static void isPoint() {
        // A tuple with w=1.0 is a point
        Matrix a = Matrix.tuple(4.3, -4.2, 3.1, 1.0);
        Assert.equals(a.getX(), 4.3);
        Assert.equals(a.getY(), -4.2);
        Assert.equals(a.getZ(), 3.1);
        Assert.equals(a.getW(), 1.0);
        Assert.withMessage(a.isPoint(), "a is a point");
        Assert.withMessage(!a.isVector(), "a is not a vector");
    }

    @Test
    public static void isVector() {
        // A tuple with w=0 is a vector
        Matrix a = Matrix.tuple(4.3, -4.2, 3.1, 0.0);
        Assert.equals(a.getX(), 4.3);
        Assert.equals(a.getY(), -4.2);
        Assert.equals(a.getZ(), 3.1);
        Assert.equals(a.getW(), 0.0);
        Assert.withMessage(!a.isPoint(), "a is not a point");
        Assert.withMessage(a.isVector(), "a is a vector");
    }

    @Test
    public static void miscToString() {
        // we add '.9' to all the numbers, because Javascript and Java will convert numbers
        // differently in those cases
        Assert.equals(Matrix.point(4.9, -4.9, 3.9).toString(), "Matrix.newPoint(4.9, -4.9, 3.9)");
        Assert.equals(Matrix.vector(4.9, -4.9, 3.9).toString(), "Matrix.newVector(4.9, -4.9, 3.9)");
        Assert.equals(Matrix.tuple(1.9, 2.9, 3.9, 4.9).toString(), "Matrix.newTuple(1.9, 2.9, 3.9, 4.9)");
    }

    @Test
    public static void createPoints() {
        // point() creates tuples with w=1
        Assert.equals(Matrix.point(4, -4, 3), Matrix.tuple(4, -4, 3, 1));
    }

    @Test
    public static void createVectors() {
        // vector() creates tuples with w=0
        Assert.equals(Matrix.vector(4, -4, 3), Matrix.tuple(4, -4, 3, 0));
    }

    @Test
    public static void addingTwoTuples() {
        Matrix a1 = Matrix.tuple(3, -2, 5, 1);
        Matrix a2 = Matrix.tuple(-2, 3, 1, 0);
        Assert.equals(a1.add(a2), Matrix.tuple(1, 1, 6, 1));
    }

    @Test
    public static void subtractingTwoPoints() {
        Matrix p1 = Matrix.point(3, 2, 1);
        Matrix p2 = Matrix.point(5, 6, 7);
        Assert.equals(p1.subtract(p2), Matrix.vector(-2, -4, -6));
    }

    @Test
    public static void subtractingAVectorFromAPoint() {
        Matrix p = Matrix.point(3, 2, 1);
        Matrix v = Matrix.vector(5, 6, 7);
        Assert.equals(p.subtract(v), Matrix.point(-2, -4, -6));
    }

    @Test
    public static void subtractingTwoVectors() {
        Matrix v1 = Matrix.vector(3, 2, 1);
        Matrix v2 = Matrix.vector(5, 6, 7);
        Assert.equals(v1.subtract(v2), Matrix.vector(-2, -4, -6));
    }

    @Test
    public static void subtractingAVectorFromTheZeroVector() {
        Matrix zero = Matrix.vector(0, 0, 0);
        Matrix v = Matrix.vector(1, -2, 3);
        Assert.equals(zero.subtract(v), Matrix.vector(-1, 2, -3));
    }

    @Test
    public static void negatingATuple() {
        Matrix a = Matrix.tuple(1, -2, 3, -4);
        Assert.equals(a.negate(), Matrix.tuple(-1, 2, -3, 4));
    }

    @Test
    public static void multiplyingATupleByAScalar() {
        Matrix a = Matrix.tuple(1, -2, 3, -4);
        Assert.equals(a.scale(3.5), Matrix.tuple(3.5, -7, 10.5, -14));
    }

    @Test
    public static void dividingATupleByAScalar() {
        Matrix a = Matrix.tuple(1, -2, 3, -4);
        Assert.equals(a.scale(0.5), Matrix.tuple(0.5, -1, 1.5, -2));
    }

    @Test
    public static void computingMagnitudes() {
        Assert.equals(Matrix.vector(1, 0, 0).magnitude(), 1.0);
        Assert.equals(Matrix.vector(0, 1, 0).magnitude(), 1.0);
        Assert.equals(Matrix.vector(0, 0, 1).magnitude(), 1.0);
        Assert.equals(Matrix.vector(1, 2, 3).magnitude(), M.pow(14, 0.5));
        Assert.almostEquals(Matrix.vector(1, 2, 3).magnitude(), M.pow(14, 0.5));
        Assert.almostEquals(Matrix.vector(-1, -2, -3).magnitude(), M.pow(14, 0.5));
    }

    @Test
    public static void normalizeVectors() {
        Matrix v = Matrix.vector(4, 0, 0);
        Assert.equals(v.normalize(), Matrix.vector(1, 0, 0));
        Assert.equals(Matrix.vector(1, 2, 3).normalize().magnitude(), 1.0);
    }

    @Test
    public static void dotProducts() {
        Matrix a = Matrix.vector(1, 2, 3);
        Matrix b = Matrix.vector(2, 3, 4);
        Assert.equals(a.dot(b), 20.0);
    }

    @Test
    public static void crossProducts() {
        Matrix a = Matrix.vector(1, 2, 3);
        Matrix b = Matrix.vector(2, 3, 4);
        Assert.equals(a.cross(b), Matrix.vector(-1, 2, -1));
        Assert.equals(b.cross(a), Matrix.vector(1, -2, 1));
    }
}
