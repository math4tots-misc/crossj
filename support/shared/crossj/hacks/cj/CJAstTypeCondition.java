package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.StrBuilder;
import crossj.base.XError;

/**
 * A condition asserting that the type resulting from a given TypeExpression has
 * the listed Traits.
 */
public final class CJAstTypeCondition implements CJAstNode {
    private final CJMark mark;
    private final CJAstTypeExpression type;
    private final List<CJAstTraitExpression> traits;

    CJAstTypeCondition(CJMark mark, CJAstTypeExpression type, List<CJAstTraitExpression> traits) {
        this.mark = mark;
        this.type = type;
        this.traits = traits;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstTypeExpression getType() {
        return type;
    }

    public List<CJAstTraitExpression> getTraits() {
        return traits;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }
}
