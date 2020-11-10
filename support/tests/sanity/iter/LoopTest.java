package sanity.iter;

import crossj.base.Assert;
import crossj.base.Str;
import crossj.base.Test;

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
            var sb = Str.builder();
            for (int i = 9; i >= 0; i--) {
                sb.i(i);
            }
            Assert.equals(sb.build(), "9876543210");
        }
        {
            var sb = Str.builder();
            for (int i = 9;; i--) {
                if (i < 6) {
                    break;
                }
                sb.i(i);
            }
            Assert.equals(sb.build(), "9876");
        }
    }

    // labeled statements are not (yet?) supported.
    // while for JS targets it would be simple to do,
    // It's not an essential a feature, and at the same
    // time, it's also not supported on some targets I
    // care about (e.g. Objective-C).
    //
    // @Test
    // public static void labelBreak() {
    //     int x = -123;
    //     out: for (int i = 0; i < 10; i++) {
    //         for (int j = 0; j < 10; j++) {
    //             if (i + j >= 15) {
    //                 x = i * j;
    //                 break out;
    //             }
    //         }
    //     }
    //     Assert.equals(x, 0);
    // }
}
