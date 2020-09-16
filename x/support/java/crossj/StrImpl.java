package crossj;

public final class StrImpl {
    private StrImpl() {}

    /**
     * See documentation on Str.codeAt for more information
     */
    public static int codeAt(String string, int index) {
        return string.charAt(index);
    }

    public static int charCode(char c) {
        return (int) c;
    }
}
