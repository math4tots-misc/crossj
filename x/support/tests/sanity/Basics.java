package sanity;

import crossj.*;

public final class Basics {

    @SuppressWarnings("unused")
    @Test
    public static void bools() {
        Assert.that(true);
        Assert.that(!false);
        Assert.that(true || true);
        Assert.that(false || true);
        Assert.that(true && true);
        Assert.that(!(false && true));
    }
}
