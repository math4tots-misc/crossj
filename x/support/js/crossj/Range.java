package crossj;

public final class Range {
    public static native XIterator<Integer> of(int start, int end);
    public static native XIterator<Integer> from(int start);
    public static native XIterator<Integer> upto(int end);
}
