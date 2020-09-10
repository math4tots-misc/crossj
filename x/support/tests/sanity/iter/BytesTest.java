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
        Assert.equals(bytes, Bytes.ofU8s(255, 254, 253));
    }

    @Test
    public static void slice() {
        Bytes bytes = Bytes.ofI8s();
        for (int i = 0; i < 100; i++) {
            bytes.addU8(i);
        }
        Assert.equals(bytes.getBytes(50, 60).list(), List.of(50, 51, 52, 53, 54, 55, 56, 57, 58, 59));
    }

    @Test
    public static void multibyte() {
        {
            // should default to little endian
            Bytes bytes = Bytes.ofI8s();
            bytes.addU16(3000);
            Assert.equals(bytes, Bytes.ofU8s(3000 % 256, 3000 / 256));
        }
        {
            // try again with big endian
            Bytes bytes = Bytes.ofI8s();
            bytes.useLittleEndian(false);
            bytes.addU16(3000);
            Assert.equals(bytes, Bytes.ofU8s(3000 / 256, 3000 % 256));
            Assert.notEquals(bytes, Bytes.ofU8s(3000 % 256, 3000 / 256));
        }
    }

    @Test
    public static void str() {
        Bytes bytes = Bytes.ofI8s(1, -2, 3, -4);
        Assert.equals(bytes.toString(), "Bytes.ofU8s(1, 254, 3, 252)");
    }
}
