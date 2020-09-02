package crossj;

public final class Pair<A, B> {
    private final A a;
    private final B b;

    private Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Pair<?, ?>)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) other;
        return a.equals(pair.a) && b.equals(pair.b);
    }

    @Override
    public int hashCode() {
        return List.of(a, b).hashCode();
    }
}
