package crossj.hacks.cj;

import crossj.base.Map;
import crossj.base.Try;

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

    @Override
    public CJIRType substitute(Map<String, CJIRType> map) {
        var newType = map.getOrNull(definition.getName());
        if (newType == null) {
            // TODO: Figure out the right thing to do here
            // throw XError.withMessage("No entry found for '" + definition.getName() + "' in substitution map");
            return this;
        } else {
            return newType;
        }
    }

    @Override
    public Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName) {
        for (var traitExpression : definition.getBounds()) {
            var trait = traitExpression.getAsIsTrait();
            var tryMethodDescriptor = trait.getMethodDescriptor(methodName);
            if (tryMethodDescriptor.isOk()) {
                return tryMethodDescriptor;
            }
        }
        return Try.fail("Method " + methodName + " not found for type variable " + definition.getName());
    }

    @Override
    public String toString() {
        return definition.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CJIRVariableType)) {
            return false;
        }
        var other = (CJIRVariableType) obj;
        return definition.getName().equals(other.definition.getName());
    }
}
