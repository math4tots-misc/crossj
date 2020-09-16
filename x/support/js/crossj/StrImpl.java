package crossj;

public final class StrImpl {
    private StrImpl() {}

    public static native int codeAt(String string, int index);

    public static native int charCode(char c);
}
