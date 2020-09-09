package sanity;

import crossj.Assert;
import crossj.Map;
import crossj.Pair;
import crossj.Test;

public final class MapTest {

    @Test
    public static void sample() {
        Map<String, Integer> map = Map.of();
        Assert.equals(map.size(), 0);
        map.put("hi", 123);
        Assert.equals(map.get("hi"), 123);
        Assert.equals(map.size(), 1);

        map = Map.of(Pair.of("a", 123), Pair.of("b", 234));
        Assert.equals(map.size(), 2);
        map.put("c", 345);
        Assert.equals(map.size(), 3);
        Assert.equals(map.getOrNull("aa"), null);
        Assert.equals(map.getOrNull("a"), 123);
        Assert.equals(map.getOrNull("b"), 234);
        Assert.equals(map.getOrNull("c"), 345);
        Assert.equals(map.removeOrFalse("bb"), false);
        Assert.equals(map.removeOrFalse("b"), true);
        Assert.equals(map.size(), 2);
        Assert.equals(map.getOrNull("a"), 123);
    }
}
