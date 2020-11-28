package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;
import crossj.base.StrBuilder;
import crossj.base.XError;

/**
 * I.e. block expressions
 */
public final class CJAstCompoundExpression implements CJAstExpression {
    private final CJMark mark;
    private final List<CJAstStatement> statements;
    private final Optional<CJAstExpression> expression;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstCompoundExpression(CJMark mark, List<CJAstStatement> statements, Optional<CJAstExpression> expression) {
        this.mark = mark;
        this.statements = statements;
        this.expression = expression;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public List<CJAstStatement> getStatements() {
        return statements;
    }

    public Optional<CJAstExpression> getExpression() {
        return expression;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO: CJAstCompoundExpression.addInspect0");
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitCompound(this, a);
    }
}
