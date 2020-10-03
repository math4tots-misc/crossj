package crossj.base;

// Roughly modeled on
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math
// and also java.lang.Math
public final class M {
    // these constants are really just placeholders,
    // since this is a native class, it should be replaced with actual
    // native values
    public static final double E = 2.718281828459045;
    public static final double PI = 3.14159265358979323846;
    public static final double TAU = PI * 2;
    public static final double INFINITY = 1.0 / 0.0;

    public static native double max(double value, double... values);

    public static native int imax(int value, int... values);

    public static native double min(double value, double... values);

    public static native int imin(int value, int... values);

    public static native int cmp(double a, double b);

    public static native int icmp(int a, int b);

    public static native double round(double value);

    public static native double floor(double value);

    public static native double ceil(double value);

    public static native double abs(double value);

    public static native int iabs(int x);

    public static native double pow(double a, double b);

    public static native int ipow(int a, int b);

    public static native double sqrt(double x);

    public static native double sin(double radians);

    public static native double cos(double radians);

    public static native double tan(double radians);

    public static native double asin(double x);

    public static native double acos(double x);

    public static native double atan(double x);

    public static native double atan2(double y, double x);

    public static native double ln(double x);

    public static native int gcd(int a, int b);
}
