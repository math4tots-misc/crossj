package crossj.hacks.cas.expr;

import crossj.base.List;

/**
 * A parenthetical expression.
 */
public final class Parenthetical implements Expression {
    private final Expression inner;

    private Parenthetical(Expression inner) {
        this.inner = inner;
    }

    public static Parenthetical of(Expression inner) {
        return new Parenthetical(inner);
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitParenthetical(this);
    }

    public Expression getInner() {
        return inner;
    }

    @Override
    public String toString() {
        return "(" + inner + ")";
    }

    @Override
    public int hashCode() {
        return List.of("Parenthetical", inner).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Parenthetical && ((Parenthetical) obj).inner.equals(inner);
    }
}
