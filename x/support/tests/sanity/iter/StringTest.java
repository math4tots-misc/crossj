package sanity.iter;

import crossj.Assert;
import crossj.Repr;
import crossj.Test;

public final class StringTest {
    @Test
    public static void stringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(i);
        }
        Assert.equals(sb.toString(), "0123456789");
    }

    @Test
    public static void stringBuilderCustomStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(new ClassWithCustomStr(i));
        }
        Assert.equals(sb.toString(), "C(0)C(1)C(2)C(3)C(4)C(5)C(6)C(7)C(8)C(9)");
    }

    @Test
    public static void customRepr() {
        Repr x = new ClassWithCustomRepr(194);
        Assert.equals(Repr.of(x), "<ClassWithCustomRepr data=194>");
    }

    @Test
    public static void stringRepr() {
        Assert.equals(Repr.of("hello\tworld"), "\"hello\\tworld\"");
    }

    @Test
    public static void implicitToString() {
        Repr x = new ClassWithCustomRepr(-7);
        Assert.equals("" + x, "<ClassWithCustomRepr xx custom toString xx >");
    }
}
