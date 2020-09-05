package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class ExpressionStatement implements Statement {
    private Node parent = null;
    private final Mark mark;
    private final Expression expression;

    public ExpressionStatement(Mark mark, Expression expression) {
        this.mark = mark;
        this.expression = expression;
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
}
