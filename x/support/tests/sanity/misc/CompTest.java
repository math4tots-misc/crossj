package sanity.misc;

import crossj.Assert;
import crossj.List;
import crossj.Test;

/**
 * See that java.lang.Comparable and implementations of it work
 * as expected
 */
public final class CompTest {

    @Test
    public static void string() {
        Assert.that("a".compareTo("b") < 0);
        Assert.less("aaa", "bbb");
        Assert.notLess("bbb", "bbb");
    }

    @Test
    public static void list() {
        List<Integer> list = List.of(333, 22, 1);
        Assert.equals(List.sorted(list), List.of(1, 22, 333));
    }
}
