package crossj.hacks.cj;

import crossj.base.List;

public final class CJIRTrait {
    private final CJAstItemDefinition definition;
    private final List<CJIRType> args;

    CJIRTrait(CJAstItemDefinition definition, List<CJIRType> args) {
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
