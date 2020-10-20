package crossj.hacks.cas.algo;

import crossj.hacks.cas.AlgebraContext;
import crossj.hacks.cas.expr.ExpressionVisitor;
import crossj.hacks.cas.expr.Fraction;
import crossj.hacks.cas.expr.IntegerLiteral;
import crossj.hacks.cas.expr.Parenthetical;
import crossj.hacks.cas.expr.Power;
import crossj.hacks.cas.expr.Product;
import crossj.hacks.cas.expr.Sum;
import crossj.hacks.cas.expr.Variable;

/**
 * Determines if a given expression is a "literal".
 *
 * That is, has no free variables.
 */
public final class IsLiteral implements ExpressionVisitor<Boolean> {
    private static final IsLiteral INSTANCE = new IsLiteral();

    private IsLiteral() {
    }

    public static IsLiteral withContext(AlgebraContext ctx) {
        return INSTANCE;
    }

    @Override
    public Boolean visitFraction(Fraction e) {
        return e.getNumerator().accept(this) && e.getDenominator().accept(this);
    }

    @Override
    public Boolean visitIntegerLiteral(IntegerLiteral e) {
        return true;
    }

    @Override
    public Boolean visitParenthetical(Parenthetical e) {
        return e.getInner().accept(this);
    }

    @Override
    public Boolean visitPower(Power e) {
        return e.getBase().accept(this) && e.getExponent().accept(this);
    }

    @Override
    public Boolean visitProduct(Product e) {
        return e.getOperands().iter().all(operand -> operand.accept(this));
    }

    @Override
    public Boolean visitSum(Sum e) {
        return e.getOperands().iter().all(operand -> operand.accept(this));
    }

    @Override
    public Boolean visitVariable(Variable e) {
        return false;
    }
}
