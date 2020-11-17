package crossj.hacks.cj;

public final class CJAstFieldDefinition implements CJAstClassMember {
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
}
