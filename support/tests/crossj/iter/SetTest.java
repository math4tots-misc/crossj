package crossj.iter;

import crossj.base.Assert;
import crossj.base.Pair;
import crossj.base.Set;
import crossj.base.Test;

public final class SetTest {
    @Test
    public static void misc() {
        Set<String> set = Set.of("a", "b", "c");
        Assert.equals(set.size(), 3);
        Assert.equals(set.iter().fold("x", (a, b) -> a + b), "xabc");
    }

    @Test
    public static void iter() {
        Set<String> set = Set.of("a", "b", "c");
        String out = "";
        for (String s : set) {
            out += s;
        }
        Assert.equals(out, "abc");
    }

    @Test
    public static void contains() {
        var set = Set.of(Pair.of(0, 2), Pair.of(3, 2), Pair.of(2, 2), Pair.of(1, 2));
        Assert.that(set.contains(Pair.of(0, 2)));
        Assert.that(set.contains(Pair.of(3, 2)));
        Assert.that(set.contains(Pair.of(2, 2)));
        Assert.that(set.contains(Pair.of(1, 2)));
    }

    @Test
    public static void equality() {
        Assert.equals(Set.of(1, 2), Set.of(1, 2));
        Assert.notEquals(Set.of(1, 2), Set.of(1, 2, 3));
    }
}
