package sanity.res;

import crossj.base.Assert;
import crossj.base.Str;
import crossj.base.Test;

public final class ResourceTest {
    @Test
    public static void checkResource() {
        var string = Str.fromUTF8(SampleResource.getData());
        Assert.equals(string, "Some sample binary resource\nsecond line\n");
    }
}
