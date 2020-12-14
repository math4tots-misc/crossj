package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Optional;
import crossj.base.StrBuilder;

public final class CJAstFieldDefinition implements CJAstItemMemberDefinition {
    private final CJMark mark;
    private final Optional<String> comment;
    private final int modifiers;
    private final boolean mutable;
    private final String name;
    private final CJAstTypeExpression type;
    private final CJAstExpression expression;

    CJAstFieldDefinition(CJMark mark, Optional<String> comment, int modifiers, boolean mutable, String name,
            CJAstTypeExpression type, CJAstExpression expression) {
        this.mark = mark;
        this.comment = comment;
        this.modifiers = modifiers;
        this.mutable = mutable;
        this.name = name;
        this.type = type;
        this.expression = expression;
        Assert.that(isStatic() == (expression != null));
        Assert.that(!isAsync());
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

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public String getName() {
        return name;
    }

    public CJAstTypeExpression getType() {
        return type;
    }

    public CJAstExpression getExpression() {
        Assert.that(expression != null);
        return expression;
    }

    @Override
    public <R, A> R accept(CJAstItemMemberVisitor<R, A> visitor, A a) {
        return visitor.visitField(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(mutable ? "var " : "val ").s(name).s(" : ").s(type.inspect0()).s(suffix).s("\n");
    }
}
