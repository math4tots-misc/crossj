package sanity;

import crossj.Assert;
import crossj.List;
import crossj.Rand;
import crossj.Test;

// TODO: Use seeded RNG
public final class RandTest {
    @Test
    public static void simpleProportions() {
        Rand rng = Rand.getDefault();
        int n = 1000;
        double le50 = 0;
        List<Integer> numbers = List.of();
        for (int i = 0; i < n; i++) {
            numbers.add(rng.getInt(0, 100));
            if (numbers.last() <= 50) {
                le50++;
            }
        }
        Assert.less(le50, 0.6 * n);
        Assert.less(0.4 * n, le50);
    }
}
