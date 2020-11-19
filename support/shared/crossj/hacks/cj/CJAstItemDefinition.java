package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Map;
import crossj.base.Pair;
import crossj.base.Str;
import crossj.base.StrBuilder;

/**
 * Class or trait definition.
 */
public final class CJAstItemDefinition implements CJAstNode {
    private final CJMark mark;
    private final String packageName;
    private final List<String> imports;
    private final int modifiers;
    private final String shortName;
    private final List<CJAstTypeParameter> typeParameters;
    private final List<CJAstTraitExpression> traits;
    private final List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> conditionalTraits;
    private final List<CJAstItemMemberDefinition> members;
    private final Map<String, String> shortToQualifiedNameMap = Map.of();

    public CJAstItemDefinition(CJMark mark, String packageName, List<String> imports, int modifiers, String shortName,
            List<CJAstTypeParameter> typeParameters, List<CJAstTraitExpression> traits,
            List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> conditionalTraits,
            List<CJAstItemMemberDefinition> members) {
        this.mark = mark;
        this.packageName = packageName;
        this.imports = imports;
        this.modifiers = modifiers;
        this.shortName = shortName;
        this.typeParameters = typeParameters;
        this.traits = traits;
        this.conditionalTraits = conditionalTraits;
        this.members = members;

        shortToQualifiedNameMap.put(shortName, getQualifiedName());
        for (var qualifiedName : imports) {
            var key = splitQualifiedName(qualifiedName).get2();
            shortToQualifiedNameMap.put(key, qualifiedName);
        }
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<String> getImports() {
        return imports;
    }

    public boolean isTrait() {
        return (modifiers & CJAstItemModifiers.TRAIT) != 0;
    }

    public boolean isNative() {
        return (modifiers & CJAstItemModifiers.NATIVE) != 0;
    }

    public String getShortName() {
        return shortName;
    }

    public String getQualifiedName() {
        return packageName + "." + shortName;
    }

    public List<CJAstTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<CJAstTraitExpression> getTraits() {
        return traits;
    }

    public List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> getConditionalTraits() {
        return conditionalTraits;
    }

    public List<CJAstItemMemberDefinition> getMembers() {
        return members;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("package ").s(packageName).s("\n");
        for (var imp : imports) {
            sb.repeatStr("  ", depth).s("import ").s(imp).s("\n");
        }

        sb.repeatStr("  ", depth).s(isTrait() ? "trait " : "class ").s(shortName).s(" {\n");
        for (var member : members) {
            member.addInspect(sb, depth + 1);
        }
        sb.repeatStr("  ", depth).s("}").s(suffix).s("\n");
    }

    /**
     * Given the context of this item definition, qualify a short item name.
     */
    public String qualifyName(String shortName) {
        return shortToQualifiedNameMap.get(shortName);
    }

    private static Pair<String, String> splitQualifiedName(String qualifiedName) {
        var parts = Str.split(qualifiedName, ".");
        return Pair.of(Str.join(".", parts.slice(0, parts.size() - 1)), parts.get(parts.size() - 1));
    }
}
