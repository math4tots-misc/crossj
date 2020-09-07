package com.github.math4tots.crossj.ast;

import crossj.List;
import crossj.Map;

public final class ReferenceType implements Type {
    private final Node usage;
    private final ClassOrInterfaceDeclaration declaration;
    private final List<Type> arguments; // nullable

    public ReferenceType(Node usage, ClassOrInterfaceDeclaration declaration, List<Type> arguments) {
        this.usage = usage;
        this.declaration = declaration;
        this.arguments = arguments;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    public List<Type> getArguments() {
        return arguments;
    }

    public boolean hasArguments() {
        return arguments != null && arguments.size() > 0;
    }

    /**
     * Returns the type binding implied by the current type.
     *
     * @return
     */
    public Map<String, Type> getTypeBinding() {
        if (!hasArguments()) {
            return Map.of();
        }
        if (!declaration.hasTypeParameters()) {
            throw usage.err(declaration.getQualifiedName() + " does not take type parameters");
        }
        List<TypeParameterDeclaration> typeParameters = declaration.getTypeParameters();
        if (typeParameters.size() != arguments.size()) {
            throw usage.err(declaration.getQualifiedName() + " expects " + typeParameters.size()
                    + " type arguments, but got " + arguments.size() + " type arguments");
        }
        Map<String, Type> binding = Map.of();
        for (int i = 0; i < arguments.size(); i++) {
            binding.put(typeParameters.get(i).getName(), arguments.get(i));
        }
        return binding;
    }

    @Override
    public Type applyBinding(Map<String, Type> binding) {
        if (!hasArguments()) {
            return this;
        }
        List<Type> newArguments = arguments.map(arg -> arg.applyBinding(binding));
        return new ReferenceType(usage, declaration, newArguments);
    }
}
