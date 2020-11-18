package crossj.hacks.cj;

import crossj.base.StrBuilder;

public final class CJAstFieldDefinition implements CJAstItemMemberDefinition {
    private final CJMark mark;
    private final int modifiers;
    private final String name;
    private final CJAstTypeExpression type;

    CJAstFieldDefinition(CJMark mark, int modifiers, String name, CJAstTypeExpression type) {
        this.mark = mark;
        this.modifiers = modifiers;
        this.name = name;
        this.type = type;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String getName() {
        return name;
    }

    public CJAstTypeExpression getType() {
        return type;
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
        sb.s("var ").s(name).s(" : ").s(type.inspect0()).s(suffix).s("\n");
    }
}
