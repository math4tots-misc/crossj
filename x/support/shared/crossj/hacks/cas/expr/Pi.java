package crossj.hacks.cas.expr;

/**
 * The special constant Pi
 */
public final class Pi implements Number {
    private static final Pi INSTANCE = new Pi();

    private Pi() {}

    public static Pi getInstance() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "pi";
    }
}
