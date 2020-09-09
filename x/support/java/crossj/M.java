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

    public static double abs(double value) {
        return Math.abs(value);
    }
}
