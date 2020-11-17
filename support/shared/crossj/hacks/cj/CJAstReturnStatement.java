package crossj.hacks.cj;

import crossj.base.StrBuilder;

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

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("return ");
        expression.addInspect0(sb, depth, false, suffix);
    }
}
