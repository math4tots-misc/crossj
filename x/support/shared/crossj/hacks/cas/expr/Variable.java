package crossj.hacks.cas.expr;

/**
 * A variable.
 *
 * Constants, if stored as a symbol, are also considered a "variable".
 */
public final class Variable implements Expression {
    private final String name;

    private Variable(String name) {
        this.name = name;
    }

    public static Variable withName(String name) {
        return new Variable(name);
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitVariable(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Variable && name.equals(((Variable) obj).name);
    }
}
