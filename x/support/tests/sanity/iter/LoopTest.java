package sanity.iter;

import crossj.Assert;
import crossj.Test;

public final class LoopTest {

    @Test
    public static void whileLoop() {
        int i = 0;
        int total = 0;
        while (i < 100) {
            total += i;
            ++i;
        }
        Assert.equals(total, 4950);

        i = 0;
        total = 2000;
        while (true) {
            if (i > 10) {
                total -= i;
                break;
            }
            i++;
        }
        Assert.equals(total, 1989);
    }

    @Test
    public static void classicForLoop() {
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 9; i >= 0; i--) {
                sb.append(i);
            }
            Assert.equals(sb.toString(), "9876543210");
        }
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 9;; i--) {
                if (i < 6) {
                    break;
                }
                sb.append(i);
            }
            Assert.equals(sb.toString(), "9876");
        }
    }
}
