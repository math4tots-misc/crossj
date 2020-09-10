package sanity.iter;

import crossj.Assert;
import crossj.Bytes;
import crossj.List;
import crossj.Test;

public final class BytesTest {
    @Test
    public static void i8ToU8() {
        Bytes bytes = Bytes.ofI8s(-1, -2, -3);
        Assert.equals(bytes.list(), List.of(255, 254, 253));
    }

    @Test
    public static void slice() {
        Bytes bytes = Bytes.ofI8s();
        for (int i = 0; i < 100; i++) {
            bytes.addU8(i);
        }
        Assert.equals(bytes.getBytes(50, 60).list(), List.of(50, 51, 52, 53, 54, 55, 56, 57, 58, 59));
    }
}
