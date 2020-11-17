package crossj.hacks.cj;

public final class CJAstReturnStatement implements CJAstStatement {
    private final CJMark mark;
    private final CJAstExpression expression;

    CJAstReturnStatement(CJMark mark, CJAstExpression expression) {
        this.mark = mark;
        this.expression = expression;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getExpression() {
        return expression;
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitReturn(this, a);
    }
}
