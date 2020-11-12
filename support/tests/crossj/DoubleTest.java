package crossj;

import crossj.base.Assert;
import crossj.base.Num;
import crossj.base.Test;

public final class DoubleTest {

    @Test
    public static void parseDoubleWithExponent() {
        Assert.equals(Num.parseDouble("12e2"), 1200.0);
        Assert.equals(Num.parseDouble("12e-2"), 0.12);
    }
}
