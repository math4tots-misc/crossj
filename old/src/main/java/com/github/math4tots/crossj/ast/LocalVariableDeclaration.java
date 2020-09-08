package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public final class LocalVariableDeclaration implements Statement, VariableDeclaration {
    private Node parent = null;
    private final Mark mark;
    private final TypeExpression type;
    private final String name;
    private final Expression initializer;

    public LocalVariableDeclaration(Mark mark, TypeExpression type, String name, Expression initializer) {
        this.mark = mark;
        this.type = type;
        this.name = name;
        this.initializer = initializer;
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

    public TypeExpression getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean hasInitializer() {
        return initializer != null;
    }

    public Expression getInitializer() {
        return initializer;
    }

    @Override
    public <R, A> R accept(StatementVisitor<R, A> visitor, A a) {
        return visitor.visitLocalVariableDeclaration(this, a);
    }
}
