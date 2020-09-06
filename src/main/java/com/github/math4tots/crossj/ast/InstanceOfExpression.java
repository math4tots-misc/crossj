package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class InstanceOfExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final Expression expression;
    private final TypeExpression type;

    public InstanceOfExpression(Mark mark, Expression expression, TypeExpression type) {
        this.mark = mark;
        this.expression = expression;
        this.type = type;
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

    public Expression getExpression() {
        return expression;
    }

    public TypeExpression getType() {
        return type;
    }

    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitInstanceOfExpression(this, a);
    }
}
