package crossj.base;

public final class XError extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public native static XError withMessage(String message);
}
