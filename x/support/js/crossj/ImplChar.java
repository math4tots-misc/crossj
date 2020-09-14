package crossj;

/**
 * Platform specific char util functions
 */
public final class ImplChar {
    private ImplChar() {}

    public static native boolean isWhitespace(char ch);
}
