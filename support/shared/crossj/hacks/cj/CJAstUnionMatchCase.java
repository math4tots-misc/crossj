package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstUnionMatchCase implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final List<String> valueNames;
    private final CJAstExpression expression;
    CJIRUnionCaseDescriptor descriptor;

    CJAstUnionMatchCase(CJMark mark, String name, List<String> valueNames, CJAstExpression expression) {
        this.mark = mark;
        this.name = name;
        this.valueNames = valueNames;
        this.expression = expression;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public List<String> getValueNames() {
        return valueNames;
    }

    public CJAstExpression getExpression() {
        return expression;
    }

    public CJIRUnionCaseDescriptor getDescriptor() {
        Assert.that(descriptor != null);
        return descriptor;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        throw XError.withMessage("TODO");
    }
}
