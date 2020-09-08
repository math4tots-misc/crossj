package java.lang;

public final class String {
    // We don't want to allow String instances to be instantiated
    // this way
    private String() {}

    public native int length();
    public native char charAt(int i);

    @Override
    public native int hashCode();
}
