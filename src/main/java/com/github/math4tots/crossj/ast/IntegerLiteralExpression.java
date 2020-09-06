package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class IntegerLiteralExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final int value;

    public IntegerLiteralExpression(Mark mark, int value) {
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

    public int getValue() {
        return value;
    }

    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitIntegerLiteralExpression(this, a);
    }
}
