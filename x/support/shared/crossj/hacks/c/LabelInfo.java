package crossj.hacks.c;

public final class LabelInfo {
    private final int label;
    private final Symbol equatedTo;

    private LabelInfo(int label, Symbol equatedTo) {
        this.label = label;
        this.equatedTo = equatedTo;
    }

    public static LabelInfo of(int label, Symbol equatedTo) {
        return new LabelInfo(label, equatedTo);
    }

    public int getLabel() {
        return label;
    }

    public Symbol getEquatedTo() {
        return equatedTo;
    }
}
