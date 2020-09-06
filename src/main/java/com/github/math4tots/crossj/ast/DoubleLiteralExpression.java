package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class DoubleLiteralExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final double value;

    public DoubleLiteralExpression(Mark mark, double value) {
        this.mark = mark;
        this.value = value;
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

    public double getValue() {
        return value;
    }

    @Override
    public <R, A> R accpet(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
