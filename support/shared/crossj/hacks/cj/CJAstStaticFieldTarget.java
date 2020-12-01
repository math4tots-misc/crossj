package crossj.hacks.cj;

import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstStaticFieldTarget implements CJAstExtendedAssignmentTarget {
    private final CJMark mark;
    private final CJAstTypeExpression owner;
    private final String name;

    CJAstStaticFieldTarget(CJMark mark, CJAstTypeExpression owner, String name) {
        this.mark = mark;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstTypeExpression getOwner() {
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
