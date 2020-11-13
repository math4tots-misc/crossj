package crossj.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.Test;

public final class RegexTest {
    @Test
    public static void matchToFirstUnmatch() {
        var re = Regex.fromPatterns("ab*x", "yy*", "z").get();
        var matcher = re.matcher("abbxyyyzzabbbbxaa");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 0);
        Assert.equals(matcher.getMatchText(), "abbx");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 1);
        Assert.equals(matcher.getMatchText(), "yyy");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 2);
        Assert.equals(matcher.getMatchText(), "z");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 2);
        Assert.equals(matcher.getMatchText(), "z");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 0);
        Assert.equals(matcher.getMatchText(), "abbbbx");
        Assert.that(!matcher.match());
        Assert.equals(matcher.getMatchIndex(), -1);
    }

    @Test
    public static void matchToEnd() {
        var re = Regex.fromPatterns("aa*", "bb*", "cc*").get();
        var matcher = re.matcher("aaaaabbbc");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 0);
        Assert.equals(matcher.getMatchText(), "aaaaa");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 1);
        Assert.equals(matcher.getMatchText(), "bbb");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchIndex(), 2);
        Assert.equals(matcher.getMatchText(), "c");
        Assert.that(!matcher.match());
    }

    @Test
    public static void matchStar() {
        var re = Regex.fromPatterns("x*").get();
        Assert.that(re.matches(""));
        Assert.that(re.matches("x"));
        Assert.that(re.matches("xx"));
        Assert.that(!re.matches("y"));
    }

    @Test
    public static void matchPlus() {
        var re = Regex.fromPatterns("x+").get();
        Assert.that(!re.matches(""));
        Assert.that(re.matches("x"));
        Assert.that(re.matches("xx"));
        Assert.that(!re.matches("y"));
    }

    @Test
    public static void emptyMatch() {
        var re = Regex.fromPatterns("x*").get();
        var matcher = re.matcher("xxx");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchText(), "xxx");
        Assert.that(matcher.match());
        Assert.equals(matcher.getMatchText(), "");
    }

    @Test
    public static void misc() {
        {
            var re = Regex.fromPatterns("aaa|bb").get();
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("aa"));
            Assert.that(!re.matches("b"));
            Assert.that(re.matches("aaa"));
            Assert.that(re.matches("bb"));
        }
        {
            var re = Regex.fromPatterns("a(xy)?b").get();
            Assert.that(re.matches("ab"));
            Assert.that(re.matches("axyb"));
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("axb"));
            Assert.that(!re.matches("axyxyb"));
        }
        {
            var re = Regex.fromPatterns("a\\?").get();
            Assert.that(re.matches("a?"));
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("a"));
            Assert.that(!re.matches("a\\?"));
        }
        {
            var re = Regex.fromPatterns(
                "abc|(01|2|3)+|(x|y|z)+",
                "777|888|999",
                "(j*|(rt+)+)*"
            ).get();
            var matcher = re.matcher("abc7770101888jrtrtttjjabcxxyzz");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 0);
            Assert.equals(matcher.getMatchText(), "abc");
            Assert.equals(matcher.getStrIter().getPosition(), 3);
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 1);
            Assert.equals(matcher.getMatchText(), "777");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 0);
            Assert.equals(matcher.getMatchText(), "0101");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 1);
            Assert.equals(matcher.getMatchText(), "888");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 2);
            Assert.equals(matcher.getMatchText(), "jrtrtttjj");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 0);
            Assert.equals(matcher.getMatchText(), "abc");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 0);
            Assert.equals(matcher.getMatchText(), "xxyzz");

            // empty match ... gets stuck here
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 2);
            Assert.equals(matcher.getMatchText(), "");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 2);
            Assert.equals(matcher.getMatchText(), "");
        }
    }

    @Test
    public static void multiway() {
        {
            var re = Regex.fromPatterns(
                "1|2|3|4|5|6|7|8|9|0",
                "a|b|c|d|e|f|g"
            ).get();
            Assert.that(!re.matches(""));
            Assert.that(re.matches("2"));
            Assert.that(re.matcher("24").match());
            Assert.that(re.matches("a"));
            Assert.that(re.matcher("a").match());
            Assert.that(re.matcher("abb").match());
        }
        {
            var re = Regex.fromPatterns(
                "(1|2|3|4|5|6|7|8|9|0)+",
                "(a|b|c|d|e|f|g)+"
            ).get();
            Assert.that(!re.matches(""));
            Assert.that(re.matches("2"));
            Assert.that(re.matcher("24").match());
            Assert.that(re.matches("a"));
            Assert.that(re.matcher("a").match());
            Assert.that(re.matcher("abb").match());

            var matcher = re.matcher("224abc99");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 0);
            Assert.equals(matcher.getMatchText(), "224");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 1);
            Assert.equals(matcher.getMatchText(), "abc");
            Assert.that(matcher.match());
            Assert.equals(matcher.getMatchIndex(), 0);
            Assert.equals(matcher.getMatchText(), "99");
            Assert.that(!matcher.match());
        }
    }

    @Test
    public static void characterClasses() {
        {
            var re = Regex.fromPatterns("a\\d+").get();
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("a"));
            Assert.that(!re.matches("55"));
            Assert.that(!re.matches("b09876123445"));
            Assert.that(!re.matches("bb"));
            Assert.that(re.matches("a1"));
            Assert.that(re.matches("a23"));
            Assert.that(re.matches("a09876123445"));
        }
        {
            var re = Regex.fromPatterns("\\w\\d+").get();
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("a"));
            Assert.that(!re.matches("bb"));
            Assert.that(re.matches("b09876123445"));
            Assert.that(re.matches("55"));
            Assert.that(re.matches("a1"));
            Assert.that(re.matches("a23"));
            Assert.that(re.matches("a09876123445"));
            Assert.that(re.matches("_09876123445"));
        }
        {
            var re = Regex.fromPatterns("[adf]+").get();
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("b"));
            Assert.that(!re.matches("c"));
            Assert.that(!re.matches("xx"));
            Assert.that(re.matches("d"));
            Assert.that(re.matches("fdaa"));
            Assert.that(re.matches("ddafd"));
        }
        {
            var re = Regex.fromPatterns("[d-z]+").get();
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("b"));
            Assert.that(!re.matches("c"));
            Assert.that(re.matches("dd"));
            Assert.that(re.matches("d"));
            Assert.that(!re.matches("fdaa"));
            Assert.that(re.matches("fd"));
            Assert.that(re.matches("ddxfd"));
        }
        {
            var re = Regex.fromPatterns("[d-z\\s]+").get();
            Assert.that(!re.matches(""));
            Assert.that(!re.matches("b"));
            Assert.that(!re.matches("c"));
            Assert.that(re.matches("dd"));
            Assert.that(re.matches("d"));
            Assert.that(!re.matches("fdaa"));
            Assert.that(re.matches("fdxx  qwer"));
            Assert.that(re.matches("fd"));
            Assert.that(re.matches("ddxfd"));
        }
    }
}
