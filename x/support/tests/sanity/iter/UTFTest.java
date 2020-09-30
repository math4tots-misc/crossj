package sanity.iter;

import crossj.Assert;
import crossj.Bytes;
import crossj.Str;
import crossj.Test;
import crossj.Tuple;

/**
 * Content here could all conceivably also live in StringTest
 */
public final class UTFTest {

    @Test
    public static void toUTF8WithbasicMultilingualPlane() {
        Assert.equals(Str.toUTF8("日"), Bytes.ofU8s(230, 151, 165));
        Assert.equals(Str.toUTF8("本"), Bytes.ofU8s(230, 156, 172));
        Assert.equals(Str.toUTF8("日本"), Bytes.ofU8s(230, 151, 165, 230, 156, 172));
    }

    @Test
    public static void toUTF8OutsideBMP() {
        Assert.equals(Str.toUTF8("𩸽"), Bytes.ofU8s(240, 169, 184, 189));
    }

    @Test
    public static void toUTF32() {
        Assert.equals(Str.toUTF32("日"), Tuple.of(26085));
        Assert.equals(Str.toUTF32("本"), Tuple.of(26412));
        Assert.equals(Str.toUTF32("日本"), Tuple.of(26085, 26412));
        Assert.equals(Str.toUTF32("𩸽"), Tuple.of(171581));
        Assert.equals(Str.toUTF32("hello"), Tuple.of(104, 101, 108, 108, 111));
    }

    @Test
    public static void fromUTF32() {
        Assert.equals(Str.fromUTF32(Tuple.of(26085)), "日");
        Assert.equals(Str.fromUTF32(Tuple.of(26412)), "本");
        Assert.equals(Str.fromUTF32(Tuple.of(26085, 26412)), "日本");
        Assert.equals(Str.fromUTF32(Tuple.of(171581)), "𩸽");
        Assert.equals(Str.fromUTF32(Tuple.of(104, 101, 108, 108, 111)), "hello");
    }
}
