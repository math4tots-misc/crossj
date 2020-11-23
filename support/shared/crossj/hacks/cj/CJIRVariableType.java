package crossj.hacks.cj;

import crossj.base.Map;
import crossj.base.Try;
import crossj.base.XError;

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
            /**
             * Substitutions should define a mapping for every type variable that appears in
             * an expression.
             *
             * In some cases, you might think that you need a partial substition, but this
             * should be avoided. For example, consider retrieving a generic method from a
             * variable whose type is a generic class. The variable's type is fully known,
             * so the substitutions related to the class itself are fully bound. But the
             * type arguments for the method's type parameters might not be known yet. In
             * this case, the substitution should be held off completely until all values
             * are known. See e.g. `CJIRMethodDescriptor`.
             */
            throw XError.withMessage("No entry found for '" + definition.getName() + "' in substitution map");
        } else {
            return newType;
        }
    }

    @Override
    public Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName) {
        for (var traitExpression : definition.getBounds()) {
            var trait = traitExpression.getAsIsTrait();
            var tryMethodDescriptor = trait.getMethodDescriptor(methodName, this);
            if (tryMethodDescriptor.isFail()) {
                return tryMethodDescriptor.castFail();
            }
            var optionMethodDescriptor = tryMethodDescriptor.get();
            if (optionMethodDescriptor.isPresent()) {
                return Try.ok(optionMethodDescriptor.get());
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
