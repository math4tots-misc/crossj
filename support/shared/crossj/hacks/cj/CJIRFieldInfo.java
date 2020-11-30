package crossj.hacks.cj;

public final class CJIRFieldInfo {
    private final boolean static_;
    private final boolean mutable;
    private final CJIRType type;

    CJIRFieldInfo(boolean static_, boolean mutable, CJIRType type) {
        this.static_ = static_;
        this.mutable = mutable;
        this.type = type;
    }

    public boolean isStatic() {
        return static_;
    }

    public boolean isMutable() {
        return mutable;
    }

    public CJIRType getType() {
        return type;
    }
}
