package crossj.iter;

public final class ClassWithCustomStr {
    private final int x;

    public ClassWithCustomStr(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "C(" + x + ")";
    }
}
