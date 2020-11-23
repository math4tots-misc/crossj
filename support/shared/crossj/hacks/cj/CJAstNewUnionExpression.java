package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstNewUnionExpression implements CJAstExpression {
    private final CJMark mark;
    private final CJAstTypeExpression type;
    private final String name;
    private final List<CJAstExpression> args;
    CJIRType resolvedType;
    CJIRUnionCaseDescriptor resolvedUnionCaseDescriptor;

    CJAstNewUnionExpression(CJMark mark, CJAstTypeExpression type, String name, List<CJAstExpression> args) {
        this.mark = mark;
        this.type = type;
        this.name = name;
        this.args = args;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstTypeExpression getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<CJAstExpression> getArguments() {
        return args;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    public CJIRUnionCaseDescriptor getResolvedUnionCaseDescriptor() {
        Assert.that(resolvedUnionCaseDescriptor != null);
        return resolvedUnionCaseDescriptor;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitNewUnion(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(type.inspect0()).s(".").s(name).s("(");
        if (args.size() > 0) {
            sb.s("\n");
            for (var arg : args) {
                arg.addInspect0(sb, depth + 1, true, ",");
            }
            sb.repeatStr("  ", depth);
        }
        sb.s(")").s(suffix).s("\n");
    }
}
