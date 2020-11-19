package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.StrBuilder;

public final class CJAstIfStatement implements CJAstStatement {
    private final CJMark mark;
    private final CJAstExpression condition;
    private final CJAstBlockStatement body;
    private final CJAstStatement other;

    CJAstIfStatement(CJMark mark, CJAstExpression condition, CJAstBlockStatement body, CJAstStatement other) {
        this.mark = mark;
        this.condition = condition;
        this.body = body;
        this.other = other;
        Assert.that(other == null || other instanceof CJAstIfStatement || other instanceof CJAstBlockStatement);
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

    public CJAstStatement getOther() {
        return other;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(suffix, "");
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("if ").s(condition.inspect0()).s("\n");
        body.addInspect0(sb, depth, true, "");
        if (other != null) {
            sb.repeatStr("  ", depth).s("else ");
            other.addInspect0(sb, depth, false, "");
        }
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitIf(this, a);
    }
}
