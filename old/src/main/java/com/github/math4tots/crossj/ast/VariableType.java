package com.github.math4tots.crossj.ast;

import crossj.Map;

public final class VariableType implements Type {
    private final TypeParameterDeclaration declaration;

    public VariableType(Node usage, TypeParameterDeclaration declaration) {
        this.declaration = declaration;
    }

    public TypeParameterDeclaration getDeclaration() {
        return declaration;
    }

    @Override
    public Type applyBinding(Map<String, Type> binding) {
        Type newType = binding.get(declaration.getName());
        return newType != null ? newType : this;
    }
}
