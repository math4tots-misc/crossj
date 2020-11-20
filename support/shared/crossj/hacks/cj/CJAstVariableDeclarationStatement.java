package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Optional;
import crossj.base.StrBuilder;

public final class CJAstVariableDeclarationStatement implements CJAstStatement {
    private final CJMark mark;
    private final String name;
    private final Optional<CJAstTypeExpression> type;
    private final CJAstExpression expression;

    CJAstVariableDeclarationStatement(CJMark mark, String name, Optional<CJAstTypeExpression> type,
            CJAstExpression expression) {
        this.mark = mark;
        this.name = name;
        this.type = type;
        this.expression = expression;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public Optional<CJAstTypeExpression> getType() {
        return type;
    }

    public CJAstExpression getExpression() {
        return expression;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(suffix, "");
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("var ").s(name);
        if (type.isPresent()) {
            sb.s(" : ").s(type.get().inspect0());
        }
        sb.s(" = ").s(expression.inspect0()).s("\n");
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitVariableDeclaration(this, a);
    }
}
