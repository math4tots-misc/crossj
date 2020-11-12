package crossj.iter;

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

    @Test
    public static void join() {
        var set = FrozenSet.join(
            FrozenSet.of("a", "b", "c"),
            FrozenSet.of("c", "d", "e")
        );
        Assert.equals(set.size(), 5);
        Assert.that(set.contains("a"));
        Assert.that(set.contains("b"));
        Assert.that(set.contains("c"));
        Assert.that(set.contains("d"));
        Assert.that(set.contains("e"));
        Assert.that(!set.contains("f"));
    }
}
