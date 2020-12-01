package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.StrBuilder;

public final class CJAstAssignmentStatement implements CJAstStatement {
    private final CJMark mark;
    private final CJAstExtendedAssignmentTarget target;
    private final CJAstExpression expression;

    CJAstAssignmentStatement(CJMark mark, CJAstExtendedAssignmentTarget target, CJAstExpression expression) {
        this.mark = mark;
        this.target = target;
        this.expression = expression;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExtendedAssignmentTarget getTarget() {
        return target;
    }

    public CJAstExpression getExpression() {
        return expression;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(suffix, "");
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(target.inspect0()).s(" = ").s(expression.inspect0()).s("\n");
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitAssignment(this, a);
    }
}
