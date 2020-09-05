package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.List;

/**
 * Type expressions
 *
 * The allowed types are:
 *
 * <li>primitive types (which is stored in 'name' field) in this case
 * 'arguments' must be null.</li>
 * <li>reference types 'arguments' may be null.</li>
 * <li>type parameter types (indistinguishable from reference types before
 * resolution)</li>
 * <li>unbounded wildcard type 'name' is '?' and 'arguments' must be null</li>
 *
 */
public final class TypeExpression implements Node {
    private Node parent;
    private final Mark mark;
    private final String name;
    private final List<TypeExpression> arguments;

    public TypeExpression(Mark mark, String name, List<TypeExpression> arguments) {
        this.parent = null;
        this.mark = mark;
        this.name = name;
        this.arguments = arguments;
        for (TypeExpression argument : arguments) {
            argument.setParent(this);
        }
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

    public String getName() {
        return name;
    }

    public List<TypeExpression> getArguments() {
        return arguments;
    }

    public boolean hasArguments() {
        return arguments != null;
    }
}
