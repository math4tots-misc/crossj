package crossj;

public final class Optional<T> {
    private final T value;

    private Optional(T value) {
        this.value = value;
    }

    public static <T> Optional<T> of(T t) {
        return new Optional<T>(t);
    }

    public static <T> Optional<T> empty() {
        return new Optional<T>(null);
    }

    public <R> Optional<R> map(Func1<R, T> f) {
        return value == null ? Optional.empty() : Optional.of(f.apply(value));
    }

    public <R> Optional<R> flatMap(Func1<Optional<R>, T> f) {
        return value == null ? Optional.empty() : f.apply(value);
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isPresent() {
        return !isEmpty();
    }

    public void ifPresent(Func1<Void, T> f) {
        if (isPresent()) {
            f.apply(value);
        }
    }

    public <R> R branch(Func1<R, T> onPresent, Func0<R> onEmpty) {
        return isPresent() ? onPresent.apply(value) : onEmpty.apply();
    }
}
