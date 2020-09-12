package crossj;

// Roughly modeled on
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math
// and also java.lang.Math
public final class M {
    // these constants are really just placeholders,
    // since this is a native class, it should be replaced with actual
    // native values
    public static final double E = Math.E;
    public static final double PI = Math.PI;
    public static final double TAU = PI * 2;

    public static double max(double value, double... values) {
        for (double x : values) {
            value = Math.max(value, x);
        }
        return value;
    }

    public static int imax(int value, int... values) {
        for (int x : values) {
            value = Math.max(value, x);
        }
        return value;
    }

    public static double min(double value, double... values) {
        for (double x : values) {
            value = Math.min(value, x);
        }
        return value;
    }

    public static int imin(int value, int... values) {
        for (int x : values) {
            value = Math.min(value, x);
        }
        return value;
    }

    public static double abs(double value) {
        return Math.abs(value);
    }

    public static int iabs(int x) {
        return Math.abs(x);
    }

    public static double pow(double a, double b) {
        return Math.pow(a, b);
    }

    public static int ipow(int a, int b) {
        return (int) Math.pow(a, b);
    }
}
