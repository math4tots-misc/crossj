package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class TypeParameterDeclaration implements TypeDeclaration {
    private Node parent = null;
    private final Mark mark;
    private final String name;
    private final TypeExpression bound; // nullable

    public TypeParameterDeclaration(Mark mark, String name, TypeExpression bound) {
        this.mark = mark;
        this.name = name;
        this.bound = bound;
        if (bound != null) {
            bound.setParent(this);
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

    public boolean hasBound() {
        return bound != null;
    }

    public TypeExpression getBound() {
        return bound;
    }
}
