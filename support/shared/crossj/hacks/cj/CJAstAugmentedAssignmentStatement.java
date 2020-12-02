package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Optional;
import crossj.base.StrBuilder;
import crossj.base.XError;

/**
 * Augmented assignment for fields and variables
 */
public final class CJAstAugmentedAssignmentStatement implements CJAstStatement {
    private final CJMark mark;
    private final Optional<CJAstExpression> owner;
    private final Optional<CJAstTypeExpression> typeOwner;
    private final String name;
    private final String type;
    private final CJAstExpression expression;

    CJAstAugmentedAssignmentStatement(CJMark mark, Optional<CJAstExpression> owner, Optional<CJAstTypeExpression> typeOwner, String name, String type,
            CJAstExpression expression) {
        Assert.that(!(owner.isPresent() && typeOwner.isPresent()));
        this.mark = mark;
        this.owner = owner;
        this.typeOwner = typeOwner;
        this.name = name;
        this.type = type;
        this.expression = expression;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public Optional<CJAstExpression> getOwner() {
        return owner;
    }

    public Optional<CJAstTypeExpression> getTypeOwner() {
        return typeOwner;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public CJAstExpression getExpression() {
        return expression;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitAugmentedAssignment(this, a);
    }
}
