package crossj.books.dragon.ch03;

/**
 * Information associated with a successful match
 */
public final class Match {
    private final String originalString;
    private final int start;
    private final int end;
    private final String tag;

    Match(String originalString, int start, int end, String tag) {
        this.originalString = originalString;
        this.start = start;
        this.end = end;
        this.tag = tag;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getTag() {
        return tag;
    }

    public String getString() {
        return originalString.substring(start, end);
    }

    @Override
    public String toString() {
        return "Match(" + getString() + ", " + start + ", " + end + ", " + tag + ")";
    }
}
