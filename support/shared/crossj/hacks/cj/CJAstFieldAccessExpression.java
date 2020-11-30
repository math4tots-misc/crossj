package crossj.hacks.cj;

import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstFieldAccessExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstExpression owner;
    private final String name;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstFieldAccessExpression(CJMark mark, CJAstExpression owner, String name) {
        this.mark = mark;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getOwner() {
        return owner;
    }

    public String getName() {
        return name;
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
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitFieldAccess(this, a);
    }
}
