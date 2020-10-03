package sanity.io;

import crossj.base.Assert;
import crossj.base.Bytes;
import crossj.base.IO;
import crossj.base.Test;

public final class FileTest {

    private static final String testFile = IO.join("out", "filefortest.txt");

    @Test
    public static void bytes() {
        // by default, when reading and writing strings, UTF-8 is assumed

        IO.writeFile(testFile, "hi");
        Bytes bytes = IO.readFileBytes(testFile);
        Assert.equals(bytes, Bytes.ofU8s(104, 105));
        Assert.equals(IO.readFile(testFile), "hi");

        IO.writeFileBytes(testFile, Bytes.ofU8s(65, 66, 67, 68, 69));
        Assert.equals(IO.readFile(testFile), "ABCDE");
        Assert.equals(IO.readFileBytes(testFile), Bytes.ofU8s(65, 66, 67, 68, 69));
    }
}
