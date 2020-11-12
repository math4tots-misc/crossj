package crossj.io;

import crossj.base.Assert;
import crossj.base.FS;
import crossj.base.Test;

public final class FSTest {
    @Test
    public static void pathJoin() {
        var sep = FS.getSeparator();
        Assert.equals(FS.join("foo", "bar", "baz"), "foo" + sep + "bar" + sep + "baz");
    }
}
