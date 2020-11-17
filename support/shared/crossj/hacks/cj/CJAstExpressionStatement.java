package crossj.hacks.cj;

public final class CJAstExpressionStatement implements CJAstStatement {
    private final CJMark mark;
    private final CJAstExpression expression;

    CJAstExpressionStatement(CJMark mark, CJAstExpression expression) {
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
}
