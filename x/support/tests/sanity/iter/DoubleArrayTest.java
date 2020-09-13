package sanity.iter;

import crossj.Assert;
import crossj.DoubleArray;
import crossj.List;
import crossj.Test;

public final class DoubleArrayTest {
    @Test
    public static void misc() {
        Assert.equals(DoubleArray.withSize(2), DoubleArray.of(0, 0));
        Assert.equals(DoubleArray.fromList(List.of(88.1, 99.2, 100.3)), DoubleArray.of(88.1, 99.2, 100.3));
        Assert.equals(DoubleArray.fromIterable(List.of(88.1, 99.2, 100.3)), DoubleArray.of(88.1, 99.2, 100.3));

        DoubleArray arr = DoubleArray.of(5.0, 6.2, 7.3, 8.4);
        Assert.equals(arr, arr);
        Assert.equals(arr, DoubleArray.of(5.0, 6.2, 7.3, 8.4));
        Assert.notEquals(arr, DoubleArray.of(5.0, 6.2, 7.3, 8.4, 9.5));
        Assert.notEquals(arr, DoubleArray.of(5.0, 6.2, 7.3, 10.4));
        Assert.equals(arr.size(), 4);
        Assert.equals(arr.get(0), 5.0);
        Assert.equals(arr.get(1), 6.2);
        Assert.equals(arr.get(2), 7.3);
        Assert.equals(arr.get(3), 8.4);
        arr.set(0, 100.1);
        Assert.equals(arr, DoubleArray.of(100.1, 6.2, 7.3, 8.4));
        Assert.equals(arr.get(0), 100.1);
        Assert.equals(arr.get(1), 6.2);
        Assert.equals(arr.get(2), 7.3);
        Assert.equals(arr.get(3), 8.4);
        Assert.equals(arr.iter().list(), List.of(100.1, 6.2, 7.3, 8.4));

        Assert.equals("" + arr, "DoubleArray.of(100.1, 6.2, 7.3, 8.4)");
        Assert.equals(arr.toString(), "DoubleArray.of(100.1, 6.2, 7.3, 8.4)");
    }

    @Test
    public static void copy() {
        DoubleArray bytes = DoubleArray.of(1, 2, 3, 4, 5);
        DoubleArray clone = bytes.clone();
        Assert.that(bytes != clone);
        Assert.equals(bytes, clone);
        clone.set(0, 500);
        Assert.equals(bytes, DoubleArray.of(1, 2, 3, 4, 5));
        Assert.equals(clone, DoubleArray.of(500, 2, 3, 4, 5));
    }
}
