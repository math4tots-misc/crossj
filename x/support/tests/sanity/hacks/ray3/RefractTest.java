package sanity.hacks.ray3;

import crossj.Assert;
import crossj.M;
import crossj.Test;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.material.Dielectric;

public final class RefractTest {
    @Test
    public static void refract() {
        // Let's suppose the surface is the zx-plane,
        // so that we can use [0, 1, 0] vector as the sufrace normal.
        var n = Matrix.vector(0, 1, 0); // normal
        {
            // s1 comes in at a 45 degree angle
            var s1 = Matrix.vector(-1, -1, 0).normalize();
            var s2 = Dielectric.computeRefractedVector(s1, n, 1.33).get();

            // i.e. angle of incidence is 45 degrees
            Assert.equals(sineAngleBetween(s1.negate(), n), M.sqrt(2) / 2);

            // s2 should be a unit vector
            Assert.withMessage(s2.isVector(), "s2 should be a vector");
            Assert.almostEquals(s2.magnitude(), 1.0);

            // Check that `sine(theta2) / sine(theta1)` is what snell's law expects.
            Assert.almostEquals(sineAngleBetween(s2.negate(), n) / sineAngleBetween(s1.negate(), n), 1.33);

            // Roughly, this is the new vector
            Assert.less(M.abs(s2.subtract(Matrix.vector(-0.94045, -0.33992, 0)).magnitude()), 0.0001);
        }
        {
            // Check out the reverse scenario
            // s1 comes in at a 45 degree angle
            var s1 = Matrix.vector(-0.94045, -0.33992, 0).normalize();
            var s2 = Dielectric.computeRefractedVector(s1, n, 1 / 1.33).get();

            // s2 should be a unit vector
            Assert.withMessage(s2.isVector(), "s2 should be a vector");
            Assert.almostEquals(s2.magnitude(), 1.0);

            // Check that `sine(theta2) / sine(theta1)` is what snell's law expects.
            Assert.almostEquals(sineAngleBetween(s2.negate(), n) / sineAngleBetween(s1.negate(), n), 1 / 1.33);

            // Roughly, we should get the 45 degree of incidence vector back
            Assert.less(M.abs(s2.subtract(Matrix.vector(-1, -1, 0).normalize()).magnitude()), 0.0001);
        }
    }

    private static double sineAngleBetween(Matrix v1, Matrix v2) {
        return v1.cross(v2).magnitude() / v1.magnitude() / v2.magnitude();
    }
}
