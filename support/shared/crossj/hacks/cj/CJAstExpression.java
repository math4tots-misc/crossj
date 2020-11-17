package crossj.hacks.cj;

public interface CJAstExpression extends CJAstNode {
    <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a);
}
