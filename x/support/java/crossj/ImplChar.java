package crossj;

/**
 * Platform specific char util functions
 */
public final class ImplChar {
    private ImplChar() {}

    public static boolean isWhitespace(char ch) {
        return Character.isWhitespace(ch);
    }
}
