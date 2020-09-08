package sanity;

import crossj.*;

public final class ListTest {

    @Test
    public static void remove() {
        List<Integer> list = List.of(1, 2, 3);

        Assert.equals(list, List.of(1, 2, 3));

        list.removeIndex(0);
        Assert.equals(list, List.of(2, 3));

        list.removeValue(3);
        Assert.equals(list, List.of(2));
    }
}
