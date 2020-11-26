package crossj.hacks.cj;

import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstForStatement implements CJAstStatement {
    private final CJMark mark;
    private final String name;
    private final CJAstExpression containerExpression;
    private final CJAstBlockStatement body;

    CJAstForStatement(CJMark mark, String name, CJAstExpression containerExpression, CJAstBlockStatement body) {
        this.mark = mark;
        this.name = name;
        this.containerExpression = containerExpression;
        this.body = body;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public CJAstExpression getContainerExpression() {
        return containerExpression;
    }

    public CJAstBlockStatement getBody() {
        return body;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO: CJAstForStatement.addInspect0");
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitFor(this, a);
    }
}
