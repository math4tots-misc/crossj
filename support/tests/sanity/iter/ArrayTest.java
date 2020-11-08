package sanity.iter;

import crossj.base.Assert;
import crossj.base.Test;

public final class ArrayTest {
    @Test
    public static void intArrayWithoutInitializer() {
        var arr = new int[10];
        Assert.equals(arr.length, 10);
        for (int i = 0; i < 10; i++) {
            Assert.equals(arr[i], 0);
        }
        arr[2] = 24;
        arr[4] = 82;
        Assert.equals(arr[0], 0);
        Assert.equals(arr[1], 0);
        Assert.equals(arr[2], 24);
        Assert.equals(arr[3], 0);
        Assert.equals(arr[4], 82);
    }

    @Test
    public static void intArrayWithInitializer() {
        var arr = new int[] { 1, 2, 3 };
        Assert.equals(arr.length, 3);
        Assert.equals(arr[0], 1);
        Assert.equals(arr[1], 2);
        Assert.equals(arr[2], 3);
        arr[0] = -12;
        Assert.equals(arr[0], -12);
        Assert.equals(arr[1], 2);
        Assert.equals(arr[2], 3);
    }
}
