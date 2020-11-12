package crossj.hacks;

import crossj.base.Assert;
import crossj.base.Bytes;
import crossj.base.Int;
import crossj.base.Test;

/**
 * References:
 * https://stackoverflow.com/questions/21110282/i-am-looking-for-standardised-test-vectors-for-a-crc32-algorithm-implementation
 */
public final class CRC32Test {
    @Test
    public static void samples() {
        // These are samples generated using Python3's zlib.crc32 function
        Assert.equals(CRC32.signed(Bytes.fromASCII("hello")), 907060870);
        Assert.equals(CRC32.signed(Bytes.fromASCII("asdf")), 1361703869);
        Assert.equals(CRC32.signed(Bytes.fromASCII("abcd")), -310194927);
        Assert.equals(CRC32.unsigned(Bytes.fromASCII("abcd")), 3984772369.0);
    }

    @Test
    public static void ironclad() {
        // Some sample tests from
        // https://github.com/froydnj/ironclad/blob/master/testing/test-vectors/crc32.testvec
        // (open source crypto library for ANSI Common LISP)
        Assert.equals(CRC32.unsigned(Bytes.fromASCII("")), Int.toUnsigned(0x00000000));
        Assert.equals(CRC32.unsigned(Bytes.fromASCII("a")), Int.toUnsigned(0xe8b7be43));
        Assert.equals(CRC32.unsigned(Bytes.fromASCII("abc")), Int.toUnsigned(0x352441c2));
        Assert.equals(CRC32.unsigned(Bytes.fromASCII("message digest")), Int.toUnsigned(0x20159d7f));
        Assert.equals(CRC32.unsigned(Bytes.fromASCII("abcdefghijklmnopqrstuvwxyz")), Int.toUnsigned(0x4c2750bd));
        Assert.equals(CRC32.unsigned(Bytes.fromASCII("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")),
                Int.toUnsigned(0x1fc2e6d2));
        Assert.equals(
                CRC32.unsigned(Bytes
                        .fromASCII("12345678901234567890123456789012345678901234567890123456789012345678901234567890")),
                Int.toUnsigned(0x7ca94a72));
    }
}
