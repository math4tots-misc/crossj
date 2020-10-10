package sanity.gameio;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.hacks.gameio.Key;
import crossj.hacks.gameio.MouseButton;

public final class KeyTest {

    @Test
    public static void toStr() {
        Assert.equals(Key.toString(Key.COMMA), ",");
        Assert.equals(Key.valueOf(","), Key.COMMA);
        Assert.equals(Key.valueOf("."), Key.PERIOD);
    }

    @Test
    public static void buttonToString() {
        Assert.equals(MouseButton.toString(MouseButton.LEFT), "Left");
        Assert.equals(MouseButton.valueOf("Left"), MouseButton.LEFT);
    }
}
