package crossj;

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

    public static native double max(double value, double... values);

    public static native int imax(int value, int... values);

    public static native double min(double value, double... values);

    public static native int imin(int value, int... values);

    public static native double abs(double value);

    public static native int iabs(int x);

    public static native double pow(double a, double b);

    public static native int ipow(int a, int b);
}
