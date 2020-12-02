package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstUnionMatchExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstExpression target;
    private final List<CJAstUnionMatchCase> cases;
    private final Optional<CJAstExpression> defaultCase;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstUnionMatchExpression(CJMark mark, CJAstExpression target, List<CJAstUnionMatchCase> cases,
            Optional<CJAstExpression> defaultCase) {
        this.mark = mark;
        this.target = target;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getTarget() {
        return target;
    }

    public List<CJAstUnionMatchCase> getCases() {
        return cases;
    }

    public Optional<CJAstExpression> getDefaultCase() {
        return defaultCase;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitUnionMatch(this, a);
    }
}
