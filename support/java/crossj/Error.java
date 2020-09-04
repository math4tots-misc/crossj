package crossj;

@SuppressWarnings("serial")
public final class Error extends RuntimeException {
    public Error(String message) {
        super(message);
    }

    public static Error withMessage(String message) {
        return new Error(message);
    }
}
