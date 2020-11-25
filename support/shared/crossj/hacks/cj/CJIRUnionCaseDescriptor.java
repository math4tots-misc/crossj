package crossj.hacks.cj;

public final class CJIRUnionCaseDescriptor {
    private final CJAstUnionCaseDefinition definition;
    private final CJIRMethodSignature signature;

    CJIRUnionCaseDescriptor(CJAstUnionCaseDefinition definition, CJIRMethodSignature signature) {
        this.definition = definition;
        this.signature = signature;
    }

    public int getTag() {
        return definition.getTag();
    }

    public String getName() {
        return definition.getName();
    }

    public CJIRMethodSignature getSignature() {
        return signature;
    }
}
