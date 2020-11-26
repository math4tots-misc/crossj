package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Str;
import crossj.base.StrBuilder;

public final class CJAstListDisplayExpression implements CJAstExpression {
    private final CJMark mark;
    private final boolean mutable;
    private final List<CJAstExpression> elements;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstListDisplayExpression(CJMark mark, boolean mutable, List<CJAstExpression> elements) {
        this.mark = mark;
        this.mutable = mutable;
        this.elements = elements;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public boolean isMutable() {
        return mutable;
    }

    public List<CJAstExpression> getElements() {
        return elements;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        if (mutable) {
            sb.s("@");
        }
        sb.s("[").s(Str.join(", ", elements.map(e -> e.inspect0()))).s("]").s(suffix).s("\n");
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitListDisplay(this, a);
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }
}
