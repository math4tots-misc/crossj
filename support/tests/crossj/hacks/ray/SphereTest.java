package crossj.hacks.ray;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.hacks.ray.geo.Sphere;
import crossj.hacks.ray.math.Matrix;

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
