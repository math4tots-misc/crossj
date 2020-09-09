package sanity.iter;

import crossj.Assert;
import crossj.List;
import crossj.Test;

public final class ListTest {

    @Test
    public static void last() {
        Assert.equals(List.of(1, 2, 3).last(), 3);
    }
}
