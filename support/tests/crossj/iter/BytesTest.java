package crossj.iter;

import crossj.base.Assert;
import crossj.base.Bytes;
import crossj.base.List;
import crossj.base.Test;

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
        Assert.equals("" + bytes, "Bytes.ofU8s(1, 254, 3, 252)");
    }

    @Test
    public static void ascii() {
        Bytes bytes = Bytes.fromASCII("hello");
        Assert.equals(bytes, Bytes.ofU8s(104, 101, 108, 108, 111));
    }

    @Test
    public static void addI32() {
        Bytes bytes = Bytes.withCapacity(24);
        int x = 2000, y = 3000, z = 1000000;
        bytes.addI32(x);
        bytes.addI32(y);
        bytes.addI32(z);
        Assert.equals(bytes, Bytes.ofU8s(
            x % 256, x / 256, 0, 0,
            y % 256, y / 256, 0, 0,
            z % 256, (z / 256) % 256, (z / 256 / 256) % 256, z / 256 / 256 / 256
        ));
    }

    @Test
    public static void copy() {
        Bytes bytes = Bytes.ofU8s(1, 2, 3, 4, 5);
        Bytes clone = bytes.clone();
        Assert.that(bytes != clone);
        Assert.equals(bytes, clone);
        clone.setU16(0, 1000);
        Assert.equals(bytes, Bytes.ofU8s(1, 2, 3, 4, 5));
        Assert.equals(clone, Bytes.ofU8s(232, 3, 3, 4, 5));
    }
}
