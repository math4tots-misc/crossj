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
public final class ClassOrInterfaceDeclaration implements TypeDeclaration {
    private final World parent;
    private final Mark mark;
    private final String packageName;
    private final List<String> imports;
    private final List<String> modifiers;
    private final boolean isInterface;
    private final String name;
    private final List<TypeParameterDeclaration> typeParameters; // nullable
    private final List<TypeExpression> interfaces;
    private final List<MemberDeclaration> members = List.of();

    public ClassOrInterfaceDeclaration(World parent, Mark mark, String packageName, List<String> imports,
            List<String> modifiers, boolean isInterface, String name, List<TypeParameterDeclaration> typeParameters,
            List<TypeExpression> interfaces) {
        this.parent = parent;
        this.mark = mark;
        this.packageName = packageName;
        this.imports = imports;
        this.modifiers = modifiers;
        this.isInterface = isInterface;
        this.name = name;
        this.typeParameters = typeParameters;
        this.interfaces = interfaces;
        parent.addTypeDeclaration(packageName + "." + name, this);
        if (typeParameters != null) {
            for (TypeParameterDeclaration declaration : typeParameters) {
                declaration.setParent(this);
            }
        }
        for (TypeExpression expression : interfaces) {
            expression.setParent(this);
        }
    }

    @Override
    public Node getParent() {
        return parent;
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
