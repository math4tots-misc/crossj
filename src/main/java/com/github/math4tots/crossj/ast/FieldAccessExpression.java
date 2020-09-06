package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class FieldAccessExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final Expression scope; // nullable
    private final String name;

    public FieldAccessExpression(Mark mark, Expression scope, String name) {
        this.mark = mark;
        this.scope = scope;
        this.name = name;
        if (scope != null) {
            scope.setParent(this);
        }
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public Mark getMark() {
        return mark;
    }

    public Expression getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitFieldAccessExpression(this, a);
    }
}
