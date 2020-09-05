package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.List;

/**
 * Class or interface
 *
 * Effectively the entire file (includes package name and import declarations)
 *
 * Nested, anonymous and non-primary types are not allowed
 */
public final class TypeDeclaration implements TypeInfo {
    private final Mark mark;
    private final String packageName;
    private final List<String> imports;
    private final List<String> modifiers;
    private final boolean isInterface;
    private final String name;
    private final List<TypeParameterDeclaration> typeParameters = List.of();
    private final List<TypeExpression> interfaces = List.of();
    private final List<MemberDeclaration> members = List.of();

    public TypeDeclaration(Mark mark, String packageName, List<String> imports, List<String> modifiers,
            boolean isInterface, String name) {
        this.mark = mark;
        this.packageName = packageName;
        this.imports = imports;
        this.modifiers = modifiers;
        this.isInterface = isInterface;
        this.name = name;
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public Mark getMark() {
        return mark;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<String> getImports() {
        return imports;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public String getName() {
        return name;
    }

    public List<TypeParameterDeclaration> getTypeParameters() {
        return typeParameters;
    }

    public List<TypeExpression> getInterfaces() {
        return interfaces;
    }

    public List<MemberDeclaration> getMembers() {
        return members;
    }
}
