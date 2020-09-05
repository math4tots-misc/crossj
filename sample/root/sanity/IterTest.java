package sanity;

import crossj.*;

public final class IterTest {

    @Test
    public static void mapAndFriends() {
        Assert.equals(
            List.of(1, 2, 3).iter().flatMap(n -> List.of("a").repeat(n)).list(),
            List.of()
        );
    }
}
