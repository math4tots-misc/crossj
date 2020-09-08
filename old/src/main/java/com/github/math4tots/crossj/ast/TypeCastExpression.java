package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class TypeCastExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final TypeExpression type;
    private final Expression expression;

    public TypeCastExpression(Mark mark, TypeExpression type, Expression expression) {
        this.mark = mark;
        this.type = type;
        this.expression = expression;
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

    public TypeExpression getType() {
        return type;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitTypeCastExpression(this, a);
    }
}
