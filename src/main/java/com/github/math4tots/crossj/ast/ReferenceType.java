package com.github.math4tots.crossj.ast;

import crossj.List;

public final class ReferenceType implements Type {
    private final ClassOrInterfaceDeclaration declaration;
    private final List<Type> arguments; // nullable

    public ReferenceType(ClassOrInterfaceDeclaration declaration, List<Type> arguments) {
        this.declaration = declaration;
        this.arguments = arguments;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    public List<Type> getArguments() {
        return arguments;
    }
}
