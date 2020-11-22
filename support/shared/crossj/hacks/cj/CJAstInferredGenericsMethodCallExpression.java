package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstInferredGenericsMethodCallExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstTypeExpression owner;
    private final String name;
    private final List<CJAstExpression> args;
    CJIRType resolvedType;
    List<CJIRType> inferredTypeArguments;

    CJAstInferredGenericsMethodCallExpression(CJMark mark, CJAstTypeExpression owner,
            String name,
            List<CJAstExpression> args) {
        this.mark = mark;
        this.owner = owner;
        this.name = name;
        this.args = args;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstTypeExpression getOwner() {
        return owner;
    }

    public List<CJAstExpression> getArguments() {
        return args;
    }

    public String getName() {
        return name;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    public List<CJIRType> getInferredTypeArguments() {
        Assert.that(inferredTypeArguments != null);
        return inferredTypeArguments;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(owner.inspect0()).s(".").s(name);
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

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitInferredGenericsMethodCall(this, a);
    }
}
