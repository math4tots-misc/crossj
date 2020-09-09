package sanity;

import crossj.Assert;
import crossj.Set;
import crossj.Test;

public final class SetTest {
    @Test
    public static void misc() {
        Set<String> set = Set.of("a", "b", "c");
        Assert.equals(set.size(), 3);
        Assert.equals(set.iter().fold("x", (a, b) -> a + b), "xabc");
    }
}
