package crossj.iter;

import crossj.base.Assert;
import crossj.base.LinkedList;
import crossj.base.List;
import crossj.base.Test;

public final class LinkedListTest {

    @Test
    public static void iter() {
        var linkedList = LinkedList.of(1, 2, 3);
        var list = List.fromIterable(linkedList);
        Assert.equals(list, List.of(1, 2, 3));
        Assert.equals(linkedList.size(), 3);
    }
}
