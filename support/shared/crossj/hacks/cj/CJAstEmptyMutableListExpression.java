package crossj.hacks.cj;

import crossj.base.StrBuilder;

public final class CJAstEmptyMutableListExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstTypeExpression type;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstEmptyMutableListExpression(CJMark mark, CJAstTypeExpression type) {
        this.mark = mark;
        this.type = type;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstTypeExpression getType() {
        return type;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("@[").s(type.inspect0()).s("]").s(suffix).s("\n");
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitEmptyMutableList(this, a);
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }
}
