package crossj.hacks.cj;

import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstConditionalExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstExpression condition;
    private final CJAstExpression left;
    private final CJAstExpression right;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstConditionalExpression(CJMark mark, CJAstExpression condition, CJAstExpression left,
            CJAstExpression right) {
        this.mark = mark;
        this.condition = condition;
        this.left = left;
        this.right = right;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getCondition() {
        return condition;
    }

    public CJAstExpression getLeft() {
        return left;
    }

    public CJAstExpression getRight() {
        return right;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
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
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitConditional(this, a);
    }
}
