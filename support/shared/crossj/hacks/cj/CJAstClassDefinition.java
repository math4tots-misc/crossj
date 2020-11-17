package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;

public final class CJAstClassDefinition implements CJAstNode {
    private final CJMark mark;
    private final String pkg;
    private final List<String> imports;
    private final int modifiers;
    private final String name;
    private final Optional<List<CJAstTypeParameter>> typeParameters;
    private final List<CJAstTypeExpression> traits;
    private final List<CJAstClassMember> members;

    public CJAstClassDefinition(CJMark mark, String pkg, List<String> imports, int modifiers, String name,
            Optional<List<CJAstTypeParameter>> typeParameters, List<CJAstTypeExpression> traits,
            List<CJAstClassMember> members) {
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

    public Optional<List<CJAstTypeParameter>> getTypeParameters() {
        return typeParameters;
    }

    public List<CJAstTypeExpression> getTraits() {
        return traits;
    }

    public List<CJAstClassMember> getMembers() {
        return members;
    }
}
