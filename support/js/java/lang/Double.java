package java.lang;

public final class Double {
    // We don't want to allow Double instances to be instantiated
    // this way
    private Double() {}

    @Override
    public native int hashCode();
}
