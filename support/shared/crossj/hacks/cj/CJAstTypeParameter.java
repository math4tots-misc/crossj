package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstTypeParameter implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final List<CJAstTraitExpression> bounds; // trait that the given type must satisfy

    CJAstTypeParameter(CJMark mark, String name, List<CJAstTraitExpression> bounds) {
        this.mark = mark;
        this.name = name;
        this.bounds = bounds;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public List<CJAstTraitExpression> getBounds() {
        return bounds;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(depth, 0);
        Assert.equals(suffix, "");
        sb.s(name);
        if (bounds.size() > 0) {
            sb.s(" : ").s(bounds.get(0).inspect());
            for (int i = 0; i < bounds.size(); i++) {
                sb.s("&").s(bounds.get(i).inspect());
            }
        }
    }
}
