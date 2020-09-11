package sanity.misc;

import crossj.Assert;
import crossj.List;
import crossj.Test;

/**
 * See that java.lang.Comparable and implementations of it work
 * as expected
 */
public final class ComparableTest {

    @Test
    public static void string() {
        Assert.that("a".compareTo("b") < 0);
        Assert.less("aaa", "bbb");
        Assert.notLess("bbb", "bbb");
    }

    @Test
    public static void list() {
        List<Integer> list1 = List.of(333, 22, 1);
        Assert.equals(List.sorted(list1), List.of(1, 22, 333));
        Assert.equals(list1, List.of(333, 22, 1));
        List<Integer> list2 = List.of(333, 22, 1);
        Assert.notLess(list1, list2);
        Assert.less(List.sorted(list1), list2);
    }
}
