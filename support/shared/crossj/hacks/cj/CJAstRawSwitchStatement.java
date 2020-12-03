package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstRawSwitchStatement implements CJAstStatement {
    private final CJMark mark;
    private final CJAstExpression target;
    private final List<CJAstRawSwitchCase> cases;
    private final Optional<CJAstBlockStatement> defaultCase;

    CJAstRawSwitchStatement(CJMark mark, CJAstExpression target, List<CJAstRawSwitchCase> cases,
            Optional<CJAstBlockStatement> defaultCase) {
        this.mark = mark;
        this.target = target;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getTarget() {
        return target;
    }

    public List<CJAstRawSwitchCase> getCases() {
        return cases;
    }

    public Optional<CJAstBlockStatement> getDefaultCase() {
        return defaultCase;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitRawSwitch(this, a);
    }
}
