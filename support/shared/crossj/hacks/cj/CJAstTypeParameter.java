package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Optional;
import crossj.base.StrBuilder;

public final class CJAstTypeParameter implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final Optional<CJAstTraitExpression> bound; // trait that the given type must satisfy

    CJAstTypeParameter(CJMark mark, String name, Optional<CJAstTraitExpression> bound) {
        this.mark = mark;
        this.name = name;
        this.bound = bound;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public Optional<CJAstTraitExpression> getBound() {
        return bound;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(depth, 0);
        Assert.equals(suffix, "");
        sb.s(name);
        if (bound.isPresent()) {
            sb.s(" : ").s(bound.get().inspect());
        }
    }
}
