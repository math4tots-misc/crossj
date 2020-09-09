package sanity.iter;

import crossj.*;

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
}
