package sanity;

import crossj.Assert;
import crossj.IO;
import crossj.Test;
import crossj.TestFinder;

/**
 * Some basic collection of tests to see that some things are working sanely
 */
public final class Main {
    public static void main(String[] args) {
        TestFinder.run("sanity");
    }

    @Test
    public static void foo() {
        Assert.<Integer>equals(1, 1);
    }
}
