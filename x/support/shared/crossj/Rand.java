package crossj;

/**
 * Random number generator.
 *
 * Not cryptographically secure. More or less based on Java's java.util.Random.
 *
 * TODO: allow seeding
 */
public final class Rand {
    private static final Rand DEFAULT = new Rand(RandImpl.getDefault());

    private final RandImpl imp;

    private Rand(RandImpl imp) {
        this.imp = imp;
    }

    public static Rand getDefault() {
        return DEFAULT;
    }

    /**
     * Returns a random number in the range [0, 1)
     */
    public double get() {
        return imp.nextDouble();
    }

    /**
     * Returns a random number in the range [a, b)
     */
    public double getDouble(double a, double b) {
        return a + (b - a) * get();
    }

    /**
     * Returns a random integer in the range [a, b)
     */
    public int getInt(int a, int b) {
        return (int) getDouble(a, b);
    }
}
