package sanity.hacks.ray3;

import crossj.Assert;
import crossj.Test;
import crossj.hacks.ray.Matrix;
import crossj.hacks.ray3.geo.Sphere;

public final class SphereTest {

    @Test
    public static void randomPoint() {
        var sphere = Sphere.withTransform(Matrix.translation(10, 10, 10));
        var point = sphere.getRandomPoint();
        Assert.order(9.0, point.getX(), 11.0);
        Assert.order(9.0, point.getY(), 11.0);
        Assert.order(9.0, point.getZ(), 11.0);
    }
}
