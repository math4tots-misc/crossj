package crossj.hacks.cas.expr;

/**
 * Visitor interface for processing expressions.
 */
public interface ExpressionVisitor<R> {
    R visitFraction(Fraction e);
    R visitIntegerLiteral(IntegerLiteral e);
    R visitParenthetical(Parenthetical e);
    R visitPower(Power e);
    R visitProduct(Product e);
    R visitSum(Sum e);
    R visitVariable(Variable e);
}
