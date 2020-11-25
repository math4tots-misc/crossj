package crossj.hacks.cj;

import crossj.base.StrBuilder;

public final class CJAstNameExpression implements CJAstExpression {
    private final CJMark mark;
    private final String name;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstNameExpression(CJMark mark, String name) {
        this.mark = mark;
        this.name = name;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitName(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(name).s(suffix).s("\n");
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }
}
