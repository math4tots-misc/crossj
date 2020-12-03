package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstRawMatchCase implements CJAstNode {
    private final CJMark mark;
    private final List<CJAstExpression> values;
    private final CJAstExpression body;

    CJAstRawMatchCase(CJMark mark, List<CJAstExpression> values, CJAstExpression body) {
        Assert.that(values.size() > 0);
        this.mark = mark;
        this.values = values;
        this.body = body;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public List<CJAstExpression> getValues() {
        return values;
    }

    public CJAstExpression getBody() {
        return body;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }
}
