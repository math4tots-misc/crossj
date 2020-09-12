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
}
