package crossj.hacks.cj;

final class CJIRLocalVariableInfo {
    private final boolean mutable;
    private final CJIRType type;

    CJIRLocalVariableInfo(boolean mutable, CJIRType type) {
        this.mutable = mutable;
        this.type = type;
    }

    public boolean isMutable() {
        return mutable;
    }

    public CJIRType getType() {
        return type;
    }
}
