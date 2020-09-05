package com.github.math4tots.crossj.ast;

public final class VariableType implements Type {
    private final TypeParameterDeclaration declaration;

    public VariableType(TypeParameterDeclaration declaration) {
        this.declaration = declaration;
    }

    public TypeParameterDeclaration getDeclaration() {
        return declaration;
    }
}
