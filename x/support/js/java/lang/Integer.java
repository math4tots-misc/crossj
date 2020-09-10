package java.lang;

public final class Integer implements Comparable<Integer> {
    // We don't want to allow Integer instances to be instantiated
    // this way
    private Integer() {}

    public native static Integer valueOf(int i);

    public native static int parseInt(String s);

    @Override
    public native int compareTo(Integer o);
}
