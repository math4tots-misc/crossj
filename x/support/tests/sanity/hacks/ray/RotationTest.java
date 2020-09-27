package sanity.hacks.ray;

import crossj.Assert;
import crossj.M;
import crossj.Test;
import crossj.hacks.ray.Matrix;

public final class RotationTest {

    @Test
    public static void sampleRotation01() {
        // Rotation axis crosses (0, 1, 0), parallel to the positive z axis.
        // Rotates 90 degrees.
        var rotation = Matrix.rotation(Matrix.point(0, 1, 0), Matrix.vector(0, 0, 1), M.TAU / 4);

        // (0, 1, 0) and any other point on the axis should stay where they are
        Assert.almostEquals(rotation.multiply(Matrix.point(0, 1, 0)), Matrix.point(0, 1, 0));
        Assert.almostEquals(rotation.multiply(Matrix.point(0, 1, 3)), Matrix.point(0, 1, 3));

        // (0, 0, 0) -> (1, 1, 0)
        Assert.almostEquals(rotation.multiply(Matrix.point(0, 0, 0)), Matrix.point(1, 1, 0));

        // (0, 0, 3) -> (1, 1, 3)
        Assert.almostEquals(rotation.multiply(Matrix.point(0, 0, 3)), Matrix.point(1, 1, 3));

        // (1, 2, 5) -> (x, y, 5)
        Assert.almostEquals(rotation.multiply(Matrix.point(1, 2, 5)), Matrix.point(-1, 2, 5));
    }

    @Test
    public static void sampleRotation02() {
        // Rotation axis is (t, t, 0) for t in R.
        // Rotates 45 degrees.
        var rotation = Matrix.rotation(Matrix.point(0, 0, 0), Matrix.vector(1, 1, 0).normalize(), M.TAU / 8);

        // Origin and any other point on the axis should stay where they are
        Assert.almostEquals(rotation.multiply(Matrix.point(0, 0, 0)), Matrix.point(0, 0, 0));
        Assert.almostEquals(rotation.multiply(Matrix.point(4, 4, 0)), Matrix.point(4, 4, 0));

        // (0, 0, 1) -> (1/2, 1/2, sqrt(2)/2)
        Assert.almostEquals(rotation.multiply(Matrix.point(0, 0, 1)), Matrix.point(0.5, -0.5, M.sqrt(2) / 2));
    }

    @Test
    public static void zeroRotation() {
        Assert.almostEquals(Matrix.yRotation(0), Matrix.identity(4));
        Assert.almostEquals(Matrix.xRotation(0), Matrix.identity(4));
        Assert.almostEquals(Matrix.zRotation(0), Matrix.identity(4));
    }
}
