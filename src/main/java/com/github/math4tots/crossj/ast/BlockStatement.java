package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.List;

public final class BlockStatement implements Statement {
    private Node parent = null;
    private final Mark mark;
    private final List<Statement> statements;

    public BlockStatement(Mark mark, List<Statement> statements) {
        this.mark = mark;
        this.statements = statements;
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

    public List<Statement> getStatements() {
        return statements;
    }
}
