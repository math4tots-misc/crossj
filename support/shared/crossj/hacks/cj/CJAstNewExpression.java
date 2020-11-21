package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstNewExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstTypeExpression type;
    private final List<CJAstExpression> args;
    CJIRType resolvedType;

    CJAstNewExpression(CJMark mark, CJAstTypeExpression type, List<CJAstExpression> args) {
        this.mark = mark;
        this.type = type;
        this.args = args;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstTypeExpression getType() {
        return type;
    }

    public List<CJAstExpression> getArguments() {
        return args;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitNew(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("new ").s(type.inspect0()).s("(\n");
        for (var arg : args) {
            arg.addInspect0(sb, depth + 1, true, ",");
        }
        sb.repeatStr("  ", depth).s(")").s(suffix).s("\n");
    }
}
