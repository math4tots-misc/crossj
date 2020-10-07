package crossj.base;

public final class StrImpl {
    private StrImpl() {
    }

    public static native int codeAt(String string, int index);

    public static native int charCode(char c);

    public static native Bytes toUTF8(String string);

    public static native String fromUTF8(Bytes bytes);

    public static native XIterator<Integer> toCodePoints(String string);

    public static native String fromCodePoints(XIterable<Integer> codePoints);

    public static native String fromSliceOfCodePoints(IntArray codePoints, int start, int end);
}
