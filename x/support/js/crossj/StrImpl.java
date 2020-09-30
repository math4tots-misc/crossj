package crossj;

public final class StrImpl {
    private StrImpl() {
    }

    public static native int codeAt(String string, int index);

    public static native int charCode(char c);

    public static native Bytes toUTF8(String string);

    public static native XIterator<Integer> toCodePoints(String string);

    public static native String fromCodePoints(XIterable<Integer> codePoints);
}
