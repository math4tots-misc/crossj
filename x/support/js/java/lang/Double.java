package java.lang;

public final class Double {
    // We don't want to allow Double instances to be instantiated
    // this way
    private Double() {}

    public native static Double valueOf(double x);

    public native static double parseDouble(String s);
}
