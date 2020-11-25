package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.XError;

public interface CJAstExpression extends CJAstNode {
    <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a);

    /**
     * Returns the resolved type of this expression.
     *
     * The expression must be first annotated with a CJIRAnnotator.
     */
    CJIRType getResolvedTypeOrNull();

    default CJIRType getResolvedType() {
        var type = getResolvedTypeOrNull();
        Assert.that(type != null);
        return type;
    }

    int getComplexityFlagsOrZero();

    default int getComplexityFlags() {
        int flags = getComplexityFlagsOrZero();
        if (flags == 0) {
            throw XError.withMessage(inspect0());
        }
        return flags;
    }
}
