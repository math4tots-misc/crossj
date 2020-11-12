package sanity.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.books.dragon.ch03.Regex;

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
    }
}
