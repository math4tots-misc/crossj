package crossj.hacks.cj;

public interface CJAstExpression extends CJAstNode {
    <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a);

    /**
     * Returns the resolved type of this expression.
     *
     * The expression must be first annotated with a CJIRAnnotator.
     */
    CJIRType getResolvedType();
}
