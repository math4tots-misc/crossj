package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.StrBuilder;

public final class CJAstNameTarget implements CJAstAssignmentTarget {
    private final CJMark mark;
    private final String name;

    public CJAstNameTarget(CJMark mark, String name) {
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
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(suffix, "");
        Assert.equals(depth, 0);
        sb.s(name);
    }
}
