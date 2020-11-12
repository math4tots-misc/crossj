package crossj.iter;

import crossj.base.Assert;
import crossj.base.Str;
import crossj.base.Test;

public final class StrBuilderTest {

    @Test
    public static void sample() {
        var sb = Str.builder();
        var s = sb.i(10).obj(" ").d(12.2).build();
        Assert.equals(s, "10 12.2");
    }

    @Test
    public static void mix() {
        Assert.equals(
                Str.builder().s("first").c(' ').codePoint(67).s(" and second").build(),
                "first C and second"
        );
    }
}
