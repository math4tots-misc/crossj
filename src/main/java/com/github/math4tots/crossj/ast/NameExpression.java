package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class NameExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final String name;

    public NameExpression(Mark mark, String name) {
        this.mark = mark;
        this.name = name;
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

    public String getName() {
        return name;
    }

    @Override
    public <R, A> R accept(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitNameExpression(this, a);
    }
}
