package crossj.hacks.c;

/**
 * A lexical token for when we process C source code.
 */
public final class Token {
    private final Mark mark;
    private final String type;
    private final Object data;

    private Token(Mark mark, String type, Object data) {
        this.mark = mark;
        this.type = type;
        this.data = data;
    }

    public static Token of(Mark mark, String type, Object data) {
        return new Token(mark, type, data);
    }

    public Mark getMark() {
        return mark;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getStringData() {
        return (String) data;
    }
}
