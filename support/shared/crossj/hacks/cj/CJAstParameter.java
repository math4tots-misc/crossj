package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.StrBuilder;

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

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(depth, 0);
        Assert.equals(suffix, "");
        sb.s(name).s(" : ").s(type.inspect());
    }
}
