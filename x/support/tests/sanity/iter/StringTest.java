package sanity.iter;

import crossj.Assert;
import crossj.List;
import crossj.Repr;
import crossj.Str;
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

    @Test
    public static void stringBuilderCustomStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(new ClassWithCustomStr(i));
        }
        Assert.equals(sb.toString(), "C(0)C(1)C(2)C(3)C(4)C(5)C(6)C(7)C(8)C(9)");
    }

    @Test
    public static void customRepr() {
        Repr x = new ClassWithCustomRepr(194);
        Assert.equals(Repr.of(x), "<ClassWithCustomRepr data=194>");
    }

    @Test
    public static void stringRepr() {
        Assert.equals(Repr.of("hello\tworld"), "\"hello\\tworld\"");
    }

    @Test
    public static void implicitToString() {
        Repr x = new ClassWithCustomRepr(-7);
        Assert.equals("" + x, "<ClassWithCustomRepr xx custom toString xx >");
    }

    @Test
    public static void startsWithEndsWith() {
        Assert.that(Str.startsWith("string", "str"));
        Assert.that(!Str.startsWith("string", "tri"));
        Assert.that(Str.startsWithAt("string", "tri", 1));
        Assert.that(Str.endsWith("string", "ing"));
        Assert.that(!Str.endsWith("string", "ri"));
        Assert.that(Str.endsWithAt("string", "ri", 4));
    }

    @Test
    public static void split() {
        Assert.equals(Str.split("", " "), List.of(""));
        Assert.equals(Str.split("Helloworld", " "), List.of("Helloworld"));
        Assert.equals(Str.split("Hello world", " "), List.of("Hello", "world"));
        Assert.equals(Str.split("a  b", " "), List.of("a", "", "b"));

        // words is a bit different from split wrt corner cases
        // (empty words are dropped)
        Assert.equals(Str.words(""), List.of());
        Assert.equals(Str.words("abc"), List.of("abc"));
        Assert.equals(Str.words("abc def"), List.of("abc", "def"));
        Assert.equals(Str.words("abc   def"), List.of("abc", "def"));
        Assert.equals(Str.words("abc   def\n"), List.of("abc", "def"));
        Assert.equals(Str.words("abc   def\n \nxxx"), List.of("abc", "def", "xxx"));
    }
}
