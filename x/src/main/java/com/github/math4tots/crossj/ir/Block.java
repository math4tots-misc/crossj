package com.github.math4tots.crossj.ir;

import crossj.Tuple;

public final class Block implements Stmt {
    private final int lineno;
    private final Tuple<Stmt> stmts;

    public Block(int lineno, Tuple<Stmt> stmts) {
        this.lineno = lineno;
        this.stmts = stmts;
    }

    @Override
    public int getLineno() {
        return lineno;
    }

    public Tuple<Stmt> getStmts() {
        return stmts;
    }
}
