package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class ReturnStatement implements Statement {
    private Node parent = null;
    private final Mark mark;
    private final Expression expression;

    public ReturnStatement(Mark mark, Expression expression) {
        this.mark = mark;
        this.expression = expression;
        expression.setParent(this);
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

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <R, A> R accept(StatementVisitor<R, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
