package sanity.iter;

import crossj.Assert;
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
}
