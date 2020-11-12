package sanity.iter;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Repr;
import crossj.base.Str;
import crossj.base.Test;

public final class StringTest {
    @Test
    public static void stringBuilder() {
        var sb = Str.builder();
        for (int i = 0; i < 10; i++) {
            sb.i(i);
        }
        Assert.equals(sb.build(), "0123456789");
    }

    @Test
    public static void stringBuilderCustomStr() {
        var sb = Str.builder();
        for (int i = 0; i < 10; i++) {
            sb.obj(new ClassWithCustomStr(i));
        }
        Assert.equals(sb.build(), "C(0)C(1)C(2)C(3)C(4)C(5)C(6)C(7)C(8)C(9)");
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

        // Split when the splitter is missing
        Assert.equals(Str.split("", "/"), List.of(""));
        Assert.equals(Str.split("abc", "/"), List.of("abc"));
        Assert.equals(Str.split("abc/", "/"), List.of("abc", ""));
        Assert.equals(Str.split("abc/def", "/"), List.of("abc", "def"));
    }

    @Test
    public static void unicode() {
        // TODO: Once we have a UTF-8 string platform I'll have to update these tests.
        //
        // Right now, all targets are UTF-16, so I assume it everywhere.
        //
        // NOTE: on some platforms (e.g. windows) "-encoding "UTF-8"" needs to be
        // explicitly
        // passed to javac to ensure
        //
        {
            // ASCII, sanity check
            String ascii = "abc";
            Assert.equals(Str.codeAt(ascii, 0), Str.code('a'));
            Assert.equals(Str.codeAt(ascii, 0), 97);
            Assert.equals(Str.codeAt(ascii, 1), Str.code('b'));
            Assert.equals(Str.codeAt(ascii, 2), Str.code('c'));
        }
        {
            // Value in BMP (should be 1 unit per-char in UTF-16)
            String s = "日本";
            Assert.equals(s.length(), 2);
            Assert.equals(Str.codeAt(s, 0), 0x65E5);
            Assert.equals(Str.codeAt(s, 0), Str.code('日'));
            Assert.equals(Str.codeAt(s, 1), Str.code('本'));
        }
        {
            // Character outside BMP
            String s = "𩸽";
            Assert.equals(Str.codeAt(s, 0), 55399);
            Assert.equals(Str.codeAt(s, 1), 56893);
            Assert.equals(s.length(), 2);
        }
    }

    @Test
    public static void strip() {
        Assert.equals(Str.strip("  hello world \n"), "hello world");
        Assert.equals(Str.strip("abcdef"), "abcdef");
    }

    @Test
    public static void unescape() {
        Assert.equals(Str.unescape("\\n\n"), "\n\n");
        Assert.equals(Str.unescape("\\n\n\\n"), "\n\n\n");
        Assert.equals(Str.unescape("\\'\\\"\\t\\n"), "'\"\t\n");
    }

    @Test
    public static void fromUTF32Slice() {
        var chars = Str.toUTF32("hello world");
        Assert.equals(Str.fromSliceOfCodePoints(chars, 6, chars.size()), "world");
    }

    @Test
    public static void strIter() {
        var iter = Str.iter("Hello world");

        Assert.that(iter.hasCodePoint());
        Assert.that(iter.startsWith("Hello"));
        Assert.that(!iter.endsWith("Hello"));
        Assert.that(!iter.endsWith("world"));
        Assert.that(iter.endsWith(""));
        Assert.that(iter.startsWith(""));

        iter.incrN("Hello".length());
        Assert.that(iter.hasCodePoint());
        Assert.that(iter.endsWith("Hello"));
        Assert.that(iter.startsWith(" world"));
        Assert.that(iter.startsWith(""));

        iter.seekToEnd();
        Assert.that(!iter.hasCodePoint());
        Assert.that(iter.endsWith("world"));
        Assert.that(iter.startsWith(""));
        Assert.that(!iter.startsWith("Hello"));

        iter.decr();
        Assert.equals(iter.peekCodePoint(), (int) 'd');
        Assert.that(iter.startsWith("d"));
        Assert.that(iter.endsWith(" worl"));
    }

    @Test
    public static void strIterSlice() {
        var iter = Str.iter("Hello world!");
        iter.seekToEnd();
        iter.decrN(" world!".length());
        Assert.equals(iter.sliceFrom(0), "Hello");

        int mark = iter.getPosition();
        Assert.equals(iter.sliceFrom(mark), "");

        iter.seekToEnd();
        Assert.equals(iter.sliceFrom(mark), " world!");

        iter.seekToStart();
        Assert.equals(iter.sliceFrom(iter.getPosition()), "");
    }

    @Test
    public static void strIterUnicode() {
        var iter = Str.iter("日本");

        Assert.that(iter.hasCodePoint());
        Assert.equals(iter.peekCodePoint(), 0x65E5); // 日

        iter.incr();
        Assert.that(iter.hasCodePoint());
        Assert.equals(iter.peekCodePoint(), 0x672c); // 本

        iter.incr();
        Assert.that(!iter.hasCodePoint());
    }

    // This is not yet supported
    // @Test
    // public static void strSwitch() {
    //     int flag = 0;
    //     switch ("Hello") {
    //         case "abc":
    //         case "hello":
    //             Assert.unreachable();
    //         case "Hello":
    //             flag |= 1;
    //         case "world":
    //             flag |= 2;
    //             break;
    //         default: {
    //             Assert.unreachable();
    //         }
    //     }
    //     Assert.that((flag & 1) != 0);
    //     Assert.that((flag & 2) != 0);
    // }
}
