package crossj.hacks.c;

public final class Mark {
    private final Source source;
    private final int lineNumber;
    private final int columnNumber;

    private Mark(Source source, int lineNumber, int columnNumber) {
        this.source = source;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public static Mark of(Source source, int lineNumber, int columnNumber) {
        return new Mark(source, lineNumber, columnNumber);
    }

    public Source getSource() {
        return source;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String format() {
        return "in " + source.getName() + " on line " + lineNumber + ", column " + columnNumber + "\n";
    }

    @Override
    public String toString() {
        return "Mark(<" + source.getName() + ">, " + lineNumber + ", " + columnNumber + ")";
    }
}
