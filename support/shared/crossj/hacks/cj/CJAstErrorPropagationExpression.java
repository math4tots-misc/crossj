package crossj.hacks.cj;

import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstErrorPropagationExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstExpression inner;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstErrorPropagationExpression(CJMark mark, CJAstExpression inner) {
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
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitErrorPropagation(this, a);
    }
}
