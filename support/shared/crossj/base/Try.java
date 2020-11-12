package crossj.base;

public final class Try<T> {
    private final T value;
    private final String message;

    private Try(T value, String message) {
        this.value = value;
        this.message = message;
    }

    public static <T> Try<T> ok(T value) {
        return new Try<T>(value, null);
    }

    public static <T> Try<T> fail(String message) {
        return new Try<T>(null, message);
    }

    public boolean isOk() {
        return message == null;
    }

    public boolean isFail() {
        return message != null;
    }

    public T get() {
        if (isFail()) {
            throw XError.withMessage("Get from a failed Try");
        }
        return value;
    }

    public T getOrElse(Func0<T> f) {
        return isOk() ? value : f.apply();
    }

    public String getErrorMessage() {
        if (isOk()) {
            throw XError.withMessage("getErrorMessage from an ok Try");
        }
        return message;
    }

    public <R> Try<R> map(Func1<R, T> f) {
        return isOk() ? ok(f.apply(value)) : fail(message);
    }

    public <R> Try<R> flatMap(Func1<Try<R>, T> f) {
        return isOk() ? f.apply(value) : fail(message);
    }

    public Try<T> mapError(Func1<String, String> f) {
        return isOk() ? this : fail(f.apply(message));
    }

    /**
     * Asserts that this Try instance is a fail, and returns this instance as a Try
     * of any desired type
     */
    public <R> Try<R> castFail() {
        if (isOk()) {
            throw XError.withMessage("Expected fail, but got ok");
        }
        return fail(message);
    }
}
