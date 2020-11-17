package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstClassDefinition implements CJAstNode {
    private final CJMark mark;
    private final String pkg;
    private final List<String> imports;
    private final int modifiers;
    private final String name;
    private final List<CJAstTypeParameter> typeParameters;
    private final List<CJAstTypeExpression> traits;
    private final List<CJAstClassMemberDefinition> members;

    public CJAstClassDefinition(CJMark mark, String pkg, List<String> imports, int modifiers, String name,
            List<CJAstTypeParameter> typeParameters, List<CJAstTypeExpression> traits, List<CJAstClassMemberDefinition> members) {
        this.mark = mark;
        this.pkg = pkg;
        this.imports = imports;
        this.modifiers = modifiers;
        this.name = name;
        this.typeParameters = typeParameters;
        this.traits = traits;
        this.members = members;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getPkg() {
        return pkg;
    }

    public List<String> getImports() {
        return imports;
    }

    public boolean isTrait() {
        return (modifiers & CJAstClassDefinitionModifiers.TRAIT) != 0;
    }

    public String getName() {
        return name;
    }

    public List<CJAstTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<CJAstTypeExpression> getTraits() {
        return traits;
    }

    public List<CJAstClassMemberDefinition> getMembers() {
        return members;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("package ").s(pkg).s("\n");
        for (var imp : imports) {
            sb.repeatStr("  ", depth).s("import ").s(imp).s("\n");
        }

        sb.repeatStr("  ", depth).s(isTrait() ? "trait " : "class ").s(name).s(" {\n");
        for (var member : members) {
            member.addInspect(sb, depth + 1);
        }
        sb.repeatStr("  ", depth).s("}").s(suffix).s("\n");
    }
}
