package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Str;
import crossj.base.StrBuilder;

/**
 * Class or trait definition.
 */
public final class CJAstItemDefinition implements CJAstNode {
    private final CJMark mark;
    private final String packageName;
    private final List<CJAstImport> imports;
    private final Optional<String> comment;
    private final int modifiers;
    private final String shortName;
    private final List<CJAstTypeParameter> typeParameters;
    private final List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> conditionalTraits;
    private final List<CJAstItemMemberDefinition> members;
    private final Map<String, String> shortToQualifiedNameMap = Map.of();
    private final CJAstTypeParameter selfTypeParameter;
    private final Map<String, CJAstUnionCaseDefinition> unionCaseCache;
    private final Map<String, CJAstItemMemberDefinition> memberDefinitionByName;
    private final String qualifiedName;
    private final boolean wrapperClass;
    List<CJIRTrait> allResolvedTraits;
    Map<String, CJIRTrait> traitsByQualifiedName;
    Map<String, CJIRIncompleteMethodDescriptor> methodMap;
    Map<String, CJIRFieldInfo> fieldMap;

    public CJAstItemDefinition(CJMark mark, String packageName, List<CJAstImport> imports, Optional<String> comment,
            int modifiers, String shortName, List<CJAstTypeParameter> typeParameters,
            List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> conditionalTraits,
            List<CJAstItemMemberDefinition> members) {
        this.mark = mark;
        this.packageName = packageName;
        this.imports = imports;
        this.comment = comment;
        this.modifiers = modifiers;
        this.shortName = shortName;
        this.typeParameters = typeParameters;
        this.conditionalTraits = conditionalTraits;
        this.members = members;
        this.qualifiedName = packageName + "." + shortName;
        this.wrapperClass = isClass() && determineWrapperClass(members);
        memberDefinitionByName = Map.fromIterable(members.map(m -> Pair.of(m.getName(), m)));

        shortToQualifiedNameMap.put(shortName, getQualifiedName());
        for (var imp : imports) {
            var key = splitQualifiedName(imp.getQualifiedName()).get2();
            shortToQualifiedNameMap.put(key, imp.getQualifiedName());
        }

        if (isTrait()) {
            var traitExpression = new CJAstTraitExpression(getMark(), getShortName(),
                    getTypeParameters().map(p -> new CJAstTypeExpression(p.getMark(), p.getName(), List.of())));
            selfTypeParameter = new CJAstTypeParameter(getMark(), "Self", List.of(traitExpression));
        } else {
            selfTypeParameter = null;
        }

        if (isUnion()) {
            var unionCaseCache = Map.<String, CJAstUnionCaseDefinition>of();
            for (var member : members) {
                if (member instanceof CJAstUnionCaseDefinition) {
                    unionCaseCache.put(member.getName(), (CJAstUnionCaseDefinition) member);
                }
            }
            this.unionCaseCache = unionCaseCache;
        } else {
            unionCaseCache = null;
        }

        Assert.withMessage(!(isTrait() && isUnion()), "An item cannot be both a trait and union");
    }

    private static boolean determineWrapperClass(List<CJAstItemMemberDefinition> members) {
        var fields = members.filter(m -> !m.isStatic() && m instanceof CJAstFieldDefinition)
                .map(f -> (CJAstFieldDefinition) f);
        return fields.size() == 1 && !fields.get(0).isMutable();
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<CJAstImport> getImports() {
        return imports;
    }

    public Optional<String> getComment() {
        return comment;
    }

    public boolean isTrait() {
        return (modifiers & CJAstItemModifiers.TRAIT) != 0;
    }

    public boolean isUnion() {
        return (modifiers & CJAstItemModifiers.UNION) != 0;
    }

    /**
     * Checks whether this item is for a class (as opposed to a trait).
     *
     * NOTE: Unions are classes.
     */
    public boolean isClass() {
        return !isTrait();
    }

    public boolean isWrapperClass() {
        return wrapperClass;
    }

    public boolean isNative() {
        return (modifiers & CJAstItemModifiers.NATIVE) != 0;
    }

    public String getShortName() {
        return shortName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public List<CJAstTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<CJAstTraitExpression> getTraits() {
        return conditionalTraits.map(pair -> pair.get1());
    }

    public List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> getConditionalTraits() {
        return conditionalTraits;
    }

    public List<CJAstItemMemberDefinition> getMembers() {
        return members;
    }

    public List<CJAstMethodDefinition> getMethods() {
        return members.filter(m -> m instanceof CJAstMethodDefinition).map(m -> (CJAstMethodDefinition) m);
    }

    public List<CJAstFieldDefinition> getFields() {
        return members.filter(m -> m instanceof CJAstFieldDefinition).map(m -> (CJAstFieldDefinition) m);
    }

    public Optional<CJAstItemMemberDefinition> getMemberDefinitionByName(String name) {
        return memberDefinitionByName.getOptional(name);
    }

    /**
     * Gets the implicit Self type parameter AST node for this item. This method
     * will throw an exception if this item is not a trait.
     *
     * ========== some background ============
     *
     * 'Self' is a way to refer to the self type from inside a trait.
     *
     * 'Self' can be used from inside classes too, but it's simpler there. In
     * classes, 'Self' is more or less an alias for the class with its type
     * parameters applied with the parameters themselves. E.g., in `List[T]`, Self
     * is equivalent to 'List[T]'.
     *
     * However, in traits, it doesn't work that way. For one thing, traits
     * themselves cannot be used as types. So in `Iterable[T]`, 'Iterable[T]' cannot
     * be used as a type (it's a trait). 'Self' should refer instead to the actual
     * class type that ultimately implements 'Iterable[T]'.
     *
     * To accomodate this, there's a 'Self' type parameter that is automatically
     * generated for each trait. Further, all classes must also provide a mechanism
     * for providing each trait it implements with the 'Self' meta object.
     *
     * This way, the 'Self' type should appear like a type parameter that implements
     * the current trait.
     */
    public CJAstTypeParameter getSelfTypeParameter() {
        Assert.that(isTrait());
        return selfTypeParameter;
    }

    /**
     * Materialize a trait object using its own type variables as its type
     * arguments. This is useful in cases when a trait needs to refer to itself
     * inside itself (i.e. for defining the "Self" alias).
     */
    public CJIRTrait getAsSelfTrait() {
        Assert.that(isTrait());
        return new CJIRTrait(this, typeParameters.map(p -> new CJIRVariableType(p, true)));
    }

    /**
     * Materialize a class type object using its own type variables as its type
     * arguments. This is useful in cases when a class needs to refer to itself
     * inside itself (i.e. for defining the "Self" alias).
     */
    public CJIRClassType getAsSelfClassType() {
        Assert.that(!isTrait());
        return new CJIRClassType(this, typeParameters.map(p -> new CJIRVariableType(p, true)));
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("package ").s(packageName).s("\n");
        for (var imp : imports) {
            imp.addInspect(sb, depth);
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

    public Optional<CJAstUnionCaseDefinition> getUnionCaseDefinitionFor(String name) {
        return unionCaseCache.getOptional(name);
    }

    public List<CJIRTrait> getAllResolvedTraits() {
        Assert.that(allResolvedTraits != null);
        return allResolvedTraits;
    }

    public Map<String, CJIRTrait> getTraitsByQualifiedName() {
        Assert.that(traitsByQualifiedName != null);
        return traitsByQualifiedName;
    }

    public Map<String, CJIRIncompleteMethodDescriptor> getMethodMap() {
        Assert.that(methodMap != null);
        return methodMap;
    }

    public Map<String, CJIRFieldInfo> getFieldMap() {
        Assert.that(fieldMap != null);
        return fieldMap;
    }
}
