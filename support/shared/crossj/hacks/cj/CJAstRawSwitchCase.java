package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstRawSwitchCase implements CJAstNode {
    private final CJMark mark;
    private final List<CJAstExpression> values;
    private final CJAstBlockStatement body;

    CJAstRawSwitchCase(CJMark mark, List<CJAstExpression> values, CJAstBlockStatement body) {
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

    public CJAstBlockStatement getBody() {
        return body;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }
}
