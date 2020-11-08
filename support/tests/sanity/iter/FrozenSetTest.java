package sanity.iter;

import crossj.base.Assert;
import crossj.base.FrozenSet;
import crossj.base.Test;

public final class FrozenSetTest {

    @Test
    public static void sample() {
        var set = FrozenSet.of(1, 2, 3);

        Assert.equals(set.size(), 3);
        Assert.that(set.contains(1));
        Assert.that(set.contains(2));
        Assert.that(set.contains(3));
        Assert.that(!set.contains(4));

        var set2 = FrozenSet.of(set, set);
        Assert.equals(set2.size(), 1);
        Assert.that(set2.contains(set));
        Assert.that(set2.contains(FrozenSet.of(1, 2, 3)));
        Assert.that(set2.contains(FrozenSet.of(1, 2, 3, 3)));
        Assert.that(!set2.contains(FrozenSet.of(1, 2, 3, 4)));
    }
}
