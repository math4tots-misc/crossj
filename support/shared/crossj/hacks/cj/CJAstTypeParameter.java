package crossj.hacks.cj;

import crossj.base.Optional;

public final class CJAstTypeParameter implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final Optional<CJAstTypeExpression> upperBound;

    CJAstTypeParameter(CJMark mark, String name, Optional<CJAstTypeExpression> upperBound) {
        this.mark = mark;
        this.name = name;
        this.upperBound = upperBound;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public Optional<CJAstTypeExpression> getUpperBound() {
        return upperBound;
    }
}
