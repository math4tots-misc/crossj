package java.lang;

public final class StringBuilder {
    // unfortunately constructors can't be labeled native
    // so we just use a stub here to indicate this exists,
    // but this class should really be considered native
    public StringBuilder() {}

    public native void append(Object object);

    @Override
    public native String toString();
}
