package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;

/**
 * The basic method call in CJ requires an explicitly specified owner type.
 *
 * E.g. Int.add(1, 2)
 *
 * Effectively, the objects themselves only contain data. In CJ, all "methods"
 * are actually classical methods on the "type" object.
 *
 * When generics are involved, the actual associated type objects are passed
 * around.
 *
 * Traits are actually interfaces implemented by the associated type object.
 *
 */
public final class CJAstMethodCallExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstTypeExpression owner;
    private final String name;
    private final Optional<List<CJAstTypeExpression>> typeArguments;
    private final List<CJAstExpression> args;

    CJAstMethodCallExpression(CJMark mark, CJAstTypeExpression owner, String name,
            Optional<List<CJAstTypeExpression>> typeArguments, List<CJAstExpression> args) {
        this.mark = mark;
        this.owner = owner;
        this.name = name;
        this.typeArguments = typeArguments;
        this.args = args;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstTypeExpression getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Optional<List<CJAstTypeExpression>> getTypeArguments() {
        return typeArguments;
    }

    public List<CJAstExpression> getArguments() {
        return args;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitMethodCall(this, a);
    }
}
