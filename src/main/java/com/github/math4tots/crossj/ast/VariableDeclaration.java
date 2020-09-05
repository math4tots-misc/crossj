package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class VariableDeclaration implements Node {
    private Node parent = null;
    private final Mark mark;
    private final TypeExpression type;
    private final String name;
    private final Expression initializer;

    public VariableDeclaration(Node parent, Mark mark, TypeExpression type, String name, Expression initializer) {
        this.parent = parent;
        this.mark = mark;
        this.type = type;
        this.name = name;
        this.initializer = initializer;
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

    public TypeExpression getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean hasInitializer() {
        return initializer != null;
    }

    public Expression getInitializer() {
        return initializer;
    }
}
