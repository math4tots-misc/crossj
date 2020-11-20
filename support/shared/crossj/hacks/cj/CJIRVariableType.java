package crossj.hacks.cj;

public final class CJIRVariableType implements CJIRType {
    private final CJAstTypeParameter definition;
    private final boolean itemLevel;

    CJIRVariableType(CJAstTypeParameter definition, boolean itemLevel) {
        this.definition = definition;
        this.itemLevel = itemLevel;
    }

    public CJAstTypeParameter getDefinition() {
        return definition;
    }

    public boolean isItemLevel() {
        return itemLevel;
    }

    public boolean isMethodLevel() {
        return !itemLevel;
    }
}
