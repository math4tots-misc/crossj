package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.List;

public final class MethodDeclaration implements MemberDeclaration {
    private ClassOrInterfaceDeclaration parent = null;
    private final Mark mark;
    private final List<String> modifiers;
    private final TypeExpression returnType;
    private final String name;
    private final List<TypeParameterDeclaration> typeParameters; // nullable
    private final List<LocalVariableDeclaration> parameters;
    private final boolean isVariadic;
    private final BlockStatement body; // nullable

    public MethodDeclaration(Mark mark, List<String> modifiers, TypeExpression returnType, String name,
            List<TypeParameterDeclaration> typeParameters, List<LocalVariableDeclaration> parameters,
            boolean isVariadic, BlockStatement body) {
        this.mark = mark;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.typeParameters = typeParameters;
        this.parameters = parameters;
        this.isVariadic = isVariadic;
        this.body = body;
        returnType.setParent(this);
        if (typeParameters != null) {
            for (TypeParameterDeclaration declaration : typeParameters) {
                declaration.setParent(this);
            }
        }
        for (LocalVariableDeclaration parameter : parameters) {
            parameter.setParent(this);
        }
        if (body != null) {
            body.setParent(this);
        }
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(ClassOrInterfaceDeclaration parent) {
        this.parent = parent;
    }

    @Override
    public Mark getMark() {
        return mark;
    }

    @Override
    public List<String> getModifiers() {
        return modifiers;
    }

    public TypeExpression getReturnType() {
        return returnType;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean hasTypeParameters() {
        return typeParameters != null && typeParameters.size() > 0;
    }

    public List<TypeParameterDeclaration> getTypeParameters() {
        return typeParameters;
    }

    public List<LocalVariableDeclaration> getParameters() {
        return parameters;
    }

    public boolean isVariadic() {
        return isVariadic;
    }

    public boolean hasBody() {
        return body != null;
    }

    public BlockStatement getBody() {
        return body;
    }

    @Override
    public TypeDeclaration lookupTypeDeclaration(String name) {
        if (typeParameters != null) {
            for (TypeParameterDeclaration declaration : typeParameters) {
                if (declaration.getName().equals(name)) {
                    return declaration;
                }
            }
        }
        return getParent().lookupTypeDeclaration(name);
    }

    @Override
    public VariableDeclaration lookupVariableDeclaration(String name) {
        for (LocalVariableDeclaration declaration : parameters) {
            if (declaration.getName().equals(name)) {
                return declaration;
            }
        }
        return null;
    }
}
