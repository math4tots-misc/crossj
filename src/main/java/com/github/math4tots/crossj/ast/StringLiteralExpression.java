package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class StringLiteralExpression implements Expression {
    private Node parent = null;
    private final Mark mark;
    private final String value;

    public StringLiteralExpression(Mark mark, String value) {
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

    public String getValue() {
        return value;
    }
}
