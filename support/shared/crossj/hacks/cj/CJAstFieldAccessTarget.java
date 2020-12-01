package crossj.hacks.cj;

import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstFieldAccessTarget implements CJAstExtendedAssignmentTarget {
    private final CJMark mark;
    private final CJAstExpression owner;
    private final String name;

    CJAstFieldAccessTarget(CJMark mark, CJAstExpression owner, String name) {
        this.mark = mark;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }
}
