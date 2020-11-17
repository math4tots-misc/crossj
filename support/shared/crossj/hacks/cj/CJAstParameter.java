package crossj.hacks.cj;

public final class CJAstParameter implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final CJAstTypeExpression type;

    CJAstParameter(CJMark mark, String name, CJAstTypeExpression type) {
        this.mark = mark;
        this.name = name;
        this.type = type;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public CJAstTypeExpression getType() {
        return type;
    }
}
