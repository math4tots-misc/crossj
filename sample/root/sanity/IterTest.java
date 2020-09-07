package sanity;

import crossj.*;

public final class IterTest {

    @Test
    public static void mapAndFriends() {
        Assert.equals(
            List.of(1, 2, 3).iter().flatMap((Integer n) -> {
                List<String> list = List.of("a");
                return list.repeat(n);
            }).list(),
            List.of("a", "a", "a", "a", "a", "a")
        );
    }
}
