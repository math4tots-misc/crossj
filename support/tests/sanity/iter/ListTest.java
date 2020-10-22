package sanity.iter;

import crossj.base.Assert;
import crossj.base.Default;
import crossj.base.List;
import crossj.base.Set;
import crossj.base.Test;

public final class ListTest {

    @Test
    public static void last() {
        Assert.equals(List.of(1, 2, 3).last(), 3);
    }

    @Test
    public static void pop() {
        List<Integer> list = List.of(1, 2, 3);
        Assert.equals(list.pop(), 3);
        Assert.equals(list.pop(), 2);
        Assert.equals(list.pop(), 1);
    }

    @Test
    public static void sortAndReverse() {
        List<Integer> list = List.of(1, 2, 3, -3, -2, -1);
        list.reverse();
        Assert.equals(list, List.of(-1, -2, -3, 3, 2, 1));
        list.reverse();
        Assert.equals(list, List.of(1, 2, 3, -3, -2, -1));
        list.sortBy(Default.comparator());
        Assert.equals(list, List.of(-3, -2, -1, 1, 2, 3));
    }

    @Test
    public static void construction() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> clone = list.clone();
        Assert.that(list != clone);
        Assert.equals(list, clone);
        list.set(0, "hi");
        Assert.notEquals(list, clone);
        Assert.equals(list, List.of("hi", "b", "c", "d"));
        Assert.equals(clone, List.of("a", "b", "c", "d"));
    }

    @Test
    public static void fromIterable() {
        List<Integer> a = List.of(1, 2, 3);
        List<Integer> b = List.fromIterable(a);
        Assert.that(a != b);
        Assert.equals(a, b);
    }

    @Test
    public static void indexOf() {
        var list = List.of("a", "aa", "b", "def", "aa");
        Assert.equals(list.indexOf("b"), 2);
        Assert.equals(list.indexOf("aa"), 1);
        Assert.equals(list.indexOf("aa"), 1);
        Assert.equals(list.lastIndexOf("b"), 2);
        Assert.equals(list.lastIndexOf("aa"), 4);
        Assert.equals(list.lastIndexOf("asdfasdf"), -1);
    }

    @Test
    public static void indexOfWithCustomEqualityClass() {
        var list = List.of(Set.of(1, 2), Set.of(55), Set.of(5, 6, 7, 8), Set.of(1, 2));
        Assert.equals(Set.of(1, 2), Set.of(1, 2));
        Assert.equals(list.indexOf(Set.of(1, 2)), 0);
        Assert.equals(list.indexOf(Set.of(5, 6, 7, 8)), 2);
        Assert.equals(list.indexOf(Set.of(1, 2, 3)), -1);
        Assert.equals(list.lastIndexOf(Set.of(1, 2)), 3);
        Assert.equals(list.lastIndexOf(Set.of(5, 6, 7, 8)), 2);
        Assert.equals(list.lastIndexOf(Set.of(1, 2, 3)), -1);
    }
}
