package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstTupleTarget implements CJAstAssignmentTarget {
    private final CJMark mark;
    private final List<CJAstAssignmentTarget> subtargets;

    CJAstTupleTarget(CJMark mark, List<CJAstAssignmentTarget> subtargets) {
        this.mark = mark;
        this.subtargets = subtargets;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public List<CJAstAssignmentTarget> getSubtargets() {
        return subtargets;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }
}
