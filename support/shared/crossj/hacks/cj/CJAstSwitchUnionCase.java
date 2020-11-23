package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Str;
import crossj.base.StrBuilder;

public final class CJAstSwitchUnionCase implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final List<String> valueNames;
    private final CJAstBlockStatement body;
    CJIRUnionCaseDescriptor descriptor;

    CJAstSwitchUnionCase(CJMark mark, String name, List<String> valueNames, CJAstBlockStatement body) {
        this.mark = mark;
        this.name = name;
        this.valueNames = valueNames;
        this.body = body;
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

    public CJAstBlockStatement getBody() {
        return body;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("case ").s(name).s("(").s(Str.join(", ", valueNames)).s(") ");
        body.addInspect0(sb, depth, false, suffix);
    }

    public CJIRUnionCaseDescriptor getDescriptor() {
        Assert.that(descriptor != null);
        return descriptor;
    }
}
