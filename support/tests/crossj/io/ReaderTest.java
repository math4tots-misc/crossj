package crossj.io;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.base.io.StringReader;
import crossj.base.io.UngetReader;

public final class ReaderTest {
    @Test
    public static void asciiStringReader() {
        var reader = StringReader.of("hello");
        Assert.equals(reader.read(), (int) 'h');
        Assert.equals(reader.read(), (int) 'e');
        Assert.equals(reader.read(), (int) 'l');
        Assert.equals(reader.read(), (int) 'l');
        Assert.equals(reader.read(), (int) 'o');
        Assert.equals(reader.read(), -1);
    }

    @Test
    public static void unicodeBMPStringReader() {
        var reader = StringReader.of("한글");
        Assert.equals(reader.read(), (int) '한');
        Assert.equals(reader.read(), (int) '글');
        Assert.equals(reader.read(), -1);
    }

    @Test
    public static void unicodeEmojisStringReader() {
        var reader = StringReader.of("😃🧘🌍");
        Assert.equals(reader.read(), 0x1F603); // grinning face with big eyes
        Assert.equals(reader.read(), 0x1F9D8); // person in lotus position
        Assert.equals(reader.read(), 0x1F30D); // globe showing Europe-Africa
        Assert.equals(reader.read(), -1);
    }

    @Test
    public static void fixedBufferUngetReader() {
        var reader = UngetReader.withFixedBuffer(2, StringReader.of("xyz"));
        Assert.equals(reader.read(), (int) 'x');
        reader.unget('a');
        Assert.equals(reader.read(), (int) 'a');
        Assert.equals(reader.read(), (int) 'y');
        reader.unget('b');
        reader.unget('c');
        Assert.equals(reader.read(), (int) 'c');
        Assert.equals(reader.read(), (int) 'b');
        Assert.equals(reader.read(), (int) 'z');
        Assert.equals(reader.read(), -1);
    }
}
