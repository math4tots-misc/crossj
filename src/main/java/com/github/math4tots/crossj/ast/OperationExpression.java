package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.*;

/**
 * Unary, binary, bitwise, logical, etc. operations The operator string contains
 * 'a', 'b', etc names to distinguish between the different kinds of operators
 * with the same symbol.
 *
 * For example, if operator is 'a+b', that's the binary '+' operator, For the
 * unary '+' operator, operator would be '+a'.
 *
 * Some nodes that would be separate literal nodes are treated as zero argument
 * operation expressions (e.g. true, false, null) for convenience.
 */
public final class OperationExpression implements Expression {
    private Node parent;
    private final Mark mark;
    private final String operator;
    private final List<Expression> arguments;

    public OperationExpression(Mark mark, String operator, List<Expression> arguments) {
        this.mark = mark;
        this.operator = operator;
        this.arguments = arguments;
        for (Expression argument : arguments) {
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

    public String getOperator() {
        return operator;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <R, A> R accpet(ExpressionVisitor<R, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
