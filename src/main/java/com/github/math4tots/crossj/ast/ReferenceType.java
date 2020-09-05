package com.github.math4tots.crossj.ast;

import crossj.List;

public final class ReferenceType implements Type {
    private final TypeDeclaration declaration;
    private final List<Type> arguments; // nullable

    public ReferenceType(TypeDeclaration declaration, List<Type> arguments) {
        this.declaration = declaration;
        this.arguments = arguments;
    }

    public TypeDeclaration getDeclaration() {
        return declaration;
    }

    public List<Type> getArguments() {
        return arguments;
    }
}
