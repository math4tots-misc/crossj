package crossj.hacks.cj;

import crossj.base.StrBuilder;

public final class CJAstLogicalNotExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstExpression inner;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstLogicalNotExpression(CJMark mark, CJAstExpression inner) {
        this.mark = mark;
        this.inner = inner;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getInner() {
        return inner;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("(not ");
        inner.addInspect0(sb, depth, false, ")" + suffix);
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitLogicalNot(this, a);
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }
}
