package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;

public final class CJAstTypeExpression implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final Optional<List<CJAstTypeExpression>> arguments;

    CJAstTypeExpression(CJMark mark, String name, Optional<List<CJAstTypeExpression>> arguments) {
        this.mark = mark;
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public Optional<List<CJAstTypeExpression>> getArguments() {
        return arguments;
    }
}
