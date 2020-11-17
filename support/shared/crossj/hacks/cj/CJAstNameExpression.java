package crossj.hacks.cj;

public final class CJAstNameExpression implements CJAstExpression {
    private final CJMark mark;
    private final String name;

    CJAstNameExpression(CJMark mark, String name) {
        this.mark = mark;
        this.name = name;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitName(this, a);
    }
}
