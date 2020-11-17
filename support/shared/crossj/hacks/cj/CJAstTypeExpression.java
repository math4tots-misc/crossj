package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;

public final class CJAstTypeExpression implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final Optional<List<CJAstTypeExpression>> args;

    CJAstTypeExpression(CJMark mark, String name, Optional<List<CJAstTypeExpression>> args) {
        this.mark = mark;
        this.name = name;
        this.args = args;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public Optional<List<CJAstTypeExpression>> getArguments() {
        return args;
    }
}
