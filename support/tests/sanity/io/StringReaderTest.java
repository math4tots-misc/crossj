package sanity.io;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.base.io.StringReader;

public final class StringReaderTest {
    @Test
    public static void ascii() {
        var reader = StringReader.of("hello");
        Assert.equals(reader.read(), (int) 'h');
        Assert.equals(reader.read(), (int) 'e');
        Assert.equals(reader.read(), (int) 'l');
        Assert.equals(reader.read(), (int) 'l');
        Assert.equals(reader.read(), (int) 'o');
        Assert.equals(reader.read(), -1);
    }

    @Test
    public static void unicodeBMP() {
        var reader = StringReader.of("한글");
        Assert.equals(reader.read(), (int) '한');
        Assert.equals(reader.read(), (int) '글');
        Assert.equals(reader.read(), -1);
    }

    @Test
    public static void unicodeEmojis() {
        var reader = StringReader.of("😃🧘🌍");
        Assert.equals(reader.read(), 0x1F603); // grinning face with big eyes
        Assert.equals(reader.read(), 0x1F9D8); // person in lotus position
        Assert.equals(reader.read(), 0x1F30D); // globe showing Europe-Africa
        Assert.equals(reader.read(), -1);
    }
}
