package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.List;

public final class MethodCallExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final Expression scope; // nullable
    private final List<TypeExpression> typeArguments; // nullable
    private final String name;
    private final List<Expression> arguments;

    public MethodCallExpression(Mark mark, Expression scope, List<TypeExpression> typeArguments, String name,
            List<Expression> arguments) {
        this.mark = mark;
        this.scope = scope;
        this.typeArguments = typeArguments;
        this.name = name;
        this.arguments = arguments;
        if (scope != null) {
            scope.setParent(this);
        }
        if (typeArguments != null) {
            for (TypeExpression type: typeArguments) {
                type.setParent(this);
            }
        }
        for (Expression argument: arguments) {
            argument.setParent(this);
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

    public boolean hasScope() {
        return scope != null;
    }

    public Expression getScope() {
        return scope;
    }

    public List<TypeExpression> getTypeArguments() {
        return typeArguments;
    }

    public boolean hasTypeArguments() {
        return typeArguments != null;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitMethodCallExpression(this, a);
    }
}
