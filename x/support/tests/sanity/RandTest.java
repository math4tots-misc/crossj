package sanity;

import crossj.Assert;
import crossj.List;
import crossj.M;
import crossj.Rand;
import crossj.Test;

// TODO: Use seeded RNG
public final class RandTest {
    @Test
    public static void simpleProportions() {
        Rand rng = Rand.getDefault();
        int n = 100;
        double le50 = 0;
        List<Integer> numbers = List.of();
        for (int i = 0; i < n; i++) {
            numbers.add(rng.nextInt(0, 100));
            if (numbers.last() <= 50) {
                le50++;
            }
        }
        Assert.less(le50, 0.7 * n);
        Assert.less(0.3 * n, le50);
    }

    @Test
    public static void gaussian() {
        int n = 100;
        Rand rng = Rand.getDefault();
        int total = 0;
        int std1 = 0;
        for (int i = 0; i < n; i++) {
            double x = rng.nextGaussian();
            if (M.abs(x) < 1) {
                std1++;
            }
            total++;
        }
        double ratio = ((double) std1) / total;
        // we expect the ratio to be ~68%.
        Assert.less(0.53, ratio);
        Assert.less(ratio, 0.83);
    }
}
