package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Optional;
import crossj.base.Str;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstUnionCaseDefinition implements CJAstItemMemberDefinition {
    private final CJMark mark;
    private final Optional<String> comment;
    private final int modifiers;
    private final String name;
    private final List<CJAstTypeExpression> valueTypes;
    int tag = -1;

    CJAstUnionCaseDefinition(CJMark mark, Optional<String> comment, int modifiers,  String name, List<CJAstTypeExpression> valueTypes) {
        Assert.equals(modifiers, 0); // for now, union cases should not have any modifiers
        this.mark = mark;
        this.comment = comment;
        this.modifiers = modifiers;
        this.name = name;
        this.valueTypes = valueTypes;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    @Override
    public Optional<String> getComment() {
        return comment;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<CJAstTypeExpression> getValueTypes() {
        return valueTypes;
    }

    @Override
    public <R, A> R accept(CJAstItemMemberVisitor<R, A> visitor, A a) {
        return visitor.visitUnionCase(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(name).s("(").s(Str.join(", ", valueTypes.map(t -> t.inspect0()))).s(")").s(suffix).s("\n");
    }

    public int getTag() {
        if (tag < 0) {
            throw XError.withMessage("UnionCase tag retrieved before being set");
        }
        return tag;
    }
}
