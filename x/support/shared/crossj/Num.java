package crossj;

/**
 * Some utilities for dealing with doubles
 */
public final class Num {
    private Num() {
    }

    public static boolean areClose(double a, double b) {
        // TODO: do something better
        return M.abs(a - b) < 0.00000000001;
    }

    public static double parseDouble(String s) {
        return Double.parseDouble(s);
    }

    public static int parseInt(String s) {
        return Integer.parseInt(s);
    }
}
