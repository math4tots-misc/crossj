package crossj.hacks.cj;

import crossj.base.StrBuilder;

public final class CJAstWhileStatement implements CJAstStatement {
    private final CJMark mark;
    private final CJAstExpression condition;
    private final CJAstBlockStatement body;

    CJAstWhileStatement(CJMark mark, CJAstExpression condition, CJAstBlockStatement body) {
        this.mark = mark;
        this.condition = condition;
        this.body = body;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getCondition() {
        return condition;
    }

    public CJAstBlockStatement getBody() {
        return body;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("while ").s(condition.inspect0());
        body.addInspect0(sb, depth, false, suffix);
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitWhile(this, a);
    }
}
