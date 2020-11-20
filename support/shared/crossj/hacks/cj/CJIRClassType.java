package crossj.hacks.cj;

import crossj.base.List;

public final class CJIRClassType implements CJIRType {
    private final CJAstItemDefinition definition;
    private final List<CJIRType> args;

    CJIRClassType(CJAstItemDefinition definition, List<CJIRType> args) {
        this.definition = definition;
        this.args = args;
    }

    public CJAstItemDefinition getDefinition() {
        return definition;
    }

    public List<CJIRType> getArguments() {
        return args;
    }
}
