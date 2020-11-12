package crossj.iter;

import crossj.base.*;

public final class IterTest {

    @Test
    public static void mapAndFriends() {
        Assert.equals(
            List.of(1, 2, 3).iter().flatMap(n -> List.of("a").repeat(n)).list(),
            List.of("a", "a", "a", "a", "a", "a")
        );
        Assert.equals(
            List.of(1, 2, 3).flatMap(n -> List.of("a").repeat(n)),
            List.of("a", "a", "a", "a", "a", "a")
        );
    }

    @Test
    public static void forEach() {
        int total = 0;
        for (int i : List.of(1, 2, 3)) {
            total += i;
        }
        Assert.equals(total, 6);
    }

    @Test
    public static void fold() {
        {
            String s = List.of("a", "bb", "ccc").fold("", (a, b) -> a + b);
            Assert.equals(s, "abbccc");
        }
    }

    @Test
    public static void anyAll() {
        Assert.that(List.of(1, 2, 3).iter().all(x -> x < 10));
        Assert.that(List.of(1, 2, 3).iter().any(x -> x > 2));
    }

    @Test
    public static void takeSkip() {
        Assert.equals(List.of(1, 2, 3, 4, 5).iter().skip(2).take(2).list(), List.of(3, 4));
        Assert.equals(List.of(1, 2, 3, 4, 5).iter().skip(1).take(10).list(), List.of(2, 3, 4, 5));
        Assert.equals(List.of(1, 2, 3, 4, 5).iter().skip(10).list(), List.of());
    }

    @Test
    public static void pop() {
        var list = List.of(1, 2, 3, 4, 5, 6);
        var iter = list.iter();
        var first = iter.pop(3);
        var rest = iter.list();
        Assert.equals(first, List.of(1, 2, 3));
        Assert.equals(rest, List.of(4, 5, 6));
    }
}
