package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.List;

/**
 * Type expressions
 *
 * The allowed types are:
 *
 * <li>primitive types (which is stored in 'name' field) in this case
 * 'arguments' must be null.</li>
 * <li>reference types 'arguments' may be null.</li>
 * <li>type parameter types (indistinguishable from reference types before
 * resolution)</li>
 * <li>unbounded wildcard type 'name' is '?' and 'arguments' must be null</li>
 *
 */
public final class TypeExpression implements Node {
    private Node parent;
    private final Mark mark;
    private final String name;
    private final List<TypeExpression> arguments;
    private Type cache = null;

    public TypeExpression(Mark mark, String name, List<TypeExpression> arguments) {
        this.parent = null;
        this.mark = mark;
        this.name = name;
        this.arguments = arguments;
        for (TypeExpression argument : arguments) {
            argument.setParent(this);
        }
    }

    @Override
    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public Mark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public List<TypeExpression> getArguments() {
        return arguments;
    }

    public boolean hasArguments() {
        return arguments != null;
    }

    public Type solveType() {
        if (cache == null) {
            cache = solveTypeNoCache();
        }
        return cache;
    }

    private Type solveTypeNoCache() {
        switch (name) {
            case "?":
                return WildcardType.INSTANCE;
            case "void":
                return PrimitiveType.VOID;
            case "char":
                return PrimitiveType.CHAR;
            case "int":
                return PrimitiveType.INT;
            case "double":
                return PrimitiveType.DOUBLE;
        }
        TypeDeclaration declaration = lookupTypeDeclarationOrThrow(name);
        if (declaration instanceof ClassOrInterfaceDeclaration) {
            return new ReferenceType((ClassOrInterfaceDeclaration) declaration, arguments.map(arg -> arg.solveType()));
        } else if (declaration instanceof TypeParameterDeclaration) {
            if (hasArguments()) {
                throw err("Type variables may not have arguments");
            }
            return new VariableType((TypeParameterDeclaration) declaration);
        }
        throw err("Unrecognized type declaration: " + declaration);
    }
}
