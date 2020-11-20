package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;

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
    private final List<CJAstTypeExpression> typeArguments;
    private final List<CJAstExpression> args;
    CJIRType resolvedType;

    CJAstMethodCallExpression(CJMark mark, CJAstTypeExpression owner, String name,
            List<CJAstTypeExpression> typeArguments, List<CJAstExpression> args) {
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

    public List<CJAstTypeExpression> getTypeArguments() {
        return typeArguments;
    }

    public List<CJAstExpression> getArguments() {
        return args;
    }

    @Override
    public CJIRType getResolvedType() {
        Assert.that(resolvedType != null);
        return resolvedType;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitMethodCall(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(owner.inspect()).s(".").s(name);
        if (typeArguments.size() > 0) {
            sb.s("[").s(typeArguments.get(0).inspect());
            for (int i = 1; i < typeArguments.size(); i++) {
                sb.s(", ").s(typeArguments.get(i).inspect());
            }
            sb.s("]");
        }
        if (args.size() > 0) {
            sb.s("(\n");
            for (var arg : args) {
                arg.addInspect0(sb, depth + 1, true, ",");
            }
            sb.repeatStr("  ", depth).s(")");
        } else {
            sb.s("()");
        }
        sb.s(suffix).s("\n");
    }
}
