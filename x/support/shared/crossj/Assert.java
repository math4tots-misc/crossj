package crossj;

public final class Assert {
    private Assert() {}

    public static void that(boolean cond) {
        if (!cond) {
            throw XError.withMessage("Assertion failed");
        }
    }

    public static <T> void equals(T a, T b) {
        if (a == null ? b != null : !a.equals(b)) {
            throw XError.withMessage("Assertion failed, expected " + a + " to equal " + b);
        }
    }

    public static void raise(Func0<Void> f) {
        boolean thrown = false;
        try {
            f.apply();
        } catch (XError e) {
            thrown = true;
        }
        if (!thrown) {
            throw XError.withMessage("Assertion failed, expected exception not raised");
        }
    }
}
