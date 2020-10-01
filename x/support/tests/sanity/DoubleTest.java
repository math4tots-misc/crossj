package sanity;

import crossj.Assert;
import crossj.Num;
import crossj.Test;

public final class DoubleTest {

    @Test
    public static void parseDoubleWithExponent() {
        Assert.equals(Num.parseDouble("12e2"), 1200.0);
        Assert.equals(Num.parseDouble("12e-2"), 0.12);
    }
}
