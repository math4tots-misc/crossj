package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstTupleDisplayExpression implements CJAstExpression {
    private final CJMark mark;
    private final List<CJAstExpression> elements;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstTupleDisplayExpression(CJMark mark, List<CJAstExpression> elements) {
        this.mark = mark;
        this.elements = elements;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public List<CJAstExpression> getElements() {
        return elements;
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO: CJAstTupleDisplayExpression.addInspect0");
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitTupleDisplay(this, a);
    }
}
