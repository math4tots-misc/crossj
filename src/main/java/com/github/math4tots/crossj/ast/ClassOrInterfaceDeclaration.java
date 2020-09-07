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
    private final List<MemberDeclaration> members;

    public ClassOrInterfaceDeclaration(World parent, Mark mark, String packageName, List<String> imports,
            List<String> modifiers, boolean isInterface, String name, List<TypeParameterDeclaration> typeParameters,
            List<TypeExpression> interfaces, List<MemberDeclaration> members) {
        this.parent = parent;
        this.mark = mark;
        this.packageName = packageName;
        this.imports = imports;
        this.modifiers = modifiers;
        this.isInterface = isInterface;
        this.name = name;
        this.typeParameters = typeParameters;
        this.interfaces = interfaces;
        this.members = members;
        parent.addTypeDeclaration(this);
        if (typeParameters != null) {
            for (TypeParameterDeclaration declaration : typeParameters) {
                declaration.setParent(this);
            }
        }
        for (TypeExpression expression : interfaces) {
            expression.setParent(this);
        }
        for (MemberDeclaration member : members) {
            member.setParent(this);
        }

        // for the most part, I don't want to do any validation with crossj itself right
        // now, but this one is so easy to miss and really easy to check, so I do it
        // here.
        if (!isInterface && !getQualifiedName().equals("java.lang.Object") && !isFinal()) {
            throw err("All crossj classes must be final");
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

    public String getQualifiedName() {
        return packageName + "." + name;
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

    public boolean isFinal() {
        return modifiers.contains("final");
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

    public List<FieldDeclaration> getFields() {
        return members.filter(m -> m instanceof FieldDeclaration).map(m -> (FieldDeclaration) m);
    }

    public List<MethodDeclaration> getMethods() {
        return members.filter(m -> m instanceof MethodDeclaration).map(m -> (MethodDeclaration) m);
    }

    @Override
    public TypeDeclaration lookupTypeDeclaration(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        for (TypeParameterDeclaration declaration : typeParameters) {
            if (declaration.getName().equals(name)) {
                return declaration;
            }
        }
        for (String qualifiedName : imports) {
            String shortName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
            if (shortName.equals("*")) {
                // this is a wildcard import
                // we check all classes in the specified package.
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                for (ClassOrInterfaceDeclaration declaration : parent.getAllDeclarationsInPackage(packageName)) {
                    if (declaration.getName().equals(name)) {
                        return declaration;
                    }
                }
            } else {
                if (shortName.equals(name)) {
                    return getParent().lookupTypeDeclaration(qualifiedName);
                }
            }
        }
        return getParent().lookupTypeDeclaration(name);
    }

    @Override
    public VariableDeclaration lookupVariableDeclaration(String name) {
        for (MemberDeclaration member : members) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) member;
                if (field.getName().equals(name)) {
                    return field;
                }
            }
        }
        return getParent().lookupVariableDeclaration(name);
    }

    @Override
    public MethodDeclaration lookupMethodDeclaration(String name) {
        for (MemberDeclaration member : members) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                if (method.getName().equals(name)) {
                    return method;
                }
            }
        }
        return getParent().lookupMethodDeclaration(name);
    }
}
