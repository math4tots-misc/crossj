package crossj.hacks.cj;

import crossj.base.List;

public final class CJAstBlockStatement implements CJAstStatement {
    private final CJMark mark;
    private final List<CJAstStatement> statements;

    public CJAstBlockStatement(CJMark mark, List<CJAstStatement> statements) {
        this.mark = mark;
        this.statements = statements;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public List<CJAstStatement> getStatements() {
        return statements;
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitBlock(this, a);
    }
}
