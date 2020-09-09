package sanity.iter;

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

    @Test
    public static void iter() {
        Set<String> set = Set.of("a", "b", "c");
        String out = "";
        for (String s : set) {
            out += s;
        }
        Assert.equals(out, "abc");
    }
}
