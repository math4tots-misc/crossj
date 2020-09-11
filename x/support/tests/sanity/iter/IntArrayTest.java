package sanity.iter;

import crossj.Assert;
import crossj.IntArray;
import crossj.List;
import crossj.Test;

public final class IntArrayTest {
    @Test
    public static void misc() {
        Assert.equals(IntArray.withSize(2), IntArray.of(0, 0));
        Assert.equals(IntArray.fromList(List.of(88, 99, 100)), IntArray.of(88, 99, 100));
        Assert.equals(IntArray.fromIterable(List.of(88, 99, 100)), IntArray.of(88, 99, 100));

        IntArray arr = IntArray.of(5, 6, 7, 8);
        Assert.equals(arr, arr);
        Assert.equals(arr, IntArray.of(5, 6, 7, 8));
        Assert.notEquals(arr, IntArray.of(5, 6, 7, 8, 9));
        Assert.notEquals(arr, IntArray.of(5, 6, 7, 10));
        Assert.equals(arr.size(), 4);
        Assert.equals(arr.get(0), 5);
        Assert.equals(arr.get(1), 6);
        Assert.equals(arr.get(2), 7);
        Assert.equals(arr.get(3), 8);
        arr.set(0, 100);
        Assert.equals(arr, IntArray.of(100, 6, 7, 8));
        Assert.equals(arr.get(0), 100);
        Assert.equals(arr.get(1), 6);
        Assert.equals(arr.get(2), 7);
        Assert.equals(arr.get(3), 8);
        Assert.equals(arr.iter().list(), List.of(100, 6, 7, 8));

        Assert.equals("" + arr, "IntArray.of(100, 6, 7, 8)");
        Assert.equals(arr.toString(), "IntArray.of(100, 6, 7, 8)");
    }
}
