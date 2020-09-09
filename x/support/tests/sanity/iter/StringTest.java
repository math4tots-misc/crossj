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
    public static void customRepr() {
        Repr x = new ClassWithCustomRepr(194);
        Assert.equals(Repr.of(x), "<ClassWithCustomRepr data=194>");
    }
}
