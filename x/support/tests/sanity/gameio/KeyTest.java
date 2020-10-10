package sanity.gameio;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.hacks.gameio.Key;

public final class KeyTest {

    @Test
    public static void toStr() {
        Assert.equals(Key.toString(Key.COMMA), ",");
    }
}
