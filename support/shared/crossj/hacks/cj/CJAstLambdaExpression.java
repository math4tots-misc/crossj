package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Str;
import crossj.base.StrBuilder;

public final class CJAstLambdaExpression implements CJAstExpression {
    private final CJMark mark;
    private final List<String> parameterNames;
    private final CJAstStatement body;
    CJIRType resolvedType;

    CJAstLambdaExpression(CJMark mark, List<String> parameterNames, CJAstStatement body) {
        this.mark = mark;
        this.parameterNames = parameterNames;
        this.body = body;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public CJAstStatement getBody() {
        return body;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("def(").s(Str.join(", ", parameterNames)).s(") {\n");
        body.addInspect0(sb, depth + 1, true, "");
        sb.repeatStr("  ", depth).s("}\n");
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitLambda(this, a);
    }
}
