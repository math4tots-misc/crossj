package sanity;

import crossj.*;

/**
 * Some basic collection of tests to see that some things are working sanely
 */
public final class Main {
    public static void main(String[] args) {
        // Unfortunately, the java version still needs some help
        // ensuring that the annotated classes are laoded
        // IterTest.class.toString();
        ListTest.class.toString();
        MapTest.class.toString();
        LexerTest.class.toString();
        TestFinder.run("sanity");
    }

    @Test
    public static void foo() {
        Assert.equals(1, 1);
    }

    @Test
    public static void classStuff() {
        // the actual string will vary by platform
        // Assert.equals(List.class.toString(), "");
    }
}
