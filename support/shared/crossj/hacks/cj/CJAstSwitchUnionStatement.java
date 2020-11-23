package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;
import crossj.base.StrBuilder;

public final class CJAstSwitchUnionStatement implements CJAstStatement {
    private final CJMark mark;
    private final CJAstExpression target;
    private final List<CJAstSwitchUnionCase> unionCases;
    private final Optional<CJAstBlockStatement> defaultBody;

    CJAstSwitchUnionStatement(CJMark mark, CJAstExpression target, List<CJAstSwitchUnionCase> unionCases,
            Optional<CJAstBlockStatement> defaultBody) {
        this.mark = mark;
        this.target = target;
        this.unionCases = unionCases;
        this.defaultBody = defaultBody;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public CJAstExpression getTarget() {
        return target;
    }

    public List<CJAstSwitchUnionCase> getUnionCases() {
        return unionCases;
    }

    public Optional<CJAstBlockStatement> getDefaultBody() {
        return defaultBody;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("switch ").s(target.inspect0()).s(" {\n");

        for (var unionCase : unionCases) {
            unionCase.addInspect0(sb, depth, true, "");
        }

        if (defaultBody.isPresent()) {
            sb.repeatStr("  ", depth).s("else ");
            defaultBody.get().addInspect0(sb, depth, false, "");
        }

        sb.repeatStr("  ", depth);
        sb.s("}").s(suffix).s("\n");
    }

    @Override
    public <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a) {
        return visitor.visitSwitchUnion(this, a);
    }
}
