package crossj.iter;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Tuple;
import crossj.base.Test;

public final class TupleTest {

    @Test
    public static void last() {
        Assert.equals(Tuple.of(1, 2, 3).last(), 3);
    }

    @Test
    public static void fromIterableWithTuple() {
        Tuple<Integer> a = Tuple.of(1, 2, 3);
        Tuple<Integer> b = Tuple.fromIterable(a);

        // Tuple.fromIterable will just return the original iterable if the iterable
        // is a Tuple.
        Assert.that(a == b);

        Assert.equals(a, b);
    }

    @Test
    public static void fromIterableWithList() {
        List<Integer> ints = List.of(1, 2, 3);
        Tuple<Integer> a = Tuple.fromIterable(ints);
        Tuple<Integer> b = Tuple.fromIterable(ints);

        Assert.that(a != b);
        Assert.equals(a, b);
    }

    @Test
    public static void slice() {
        Tuple<Integer> a = Tuple.of(1, 2, 3, 4);
        Assert.equals(a.slice(0, 3), Tuple.of(1, 2, 3));
        Assert.equals(a.sliceUpto(2), Tuple.of(1, 2));
        Assert.equals(a.sliceFrom(2), Tuple.of(3, 4));
    }
}
