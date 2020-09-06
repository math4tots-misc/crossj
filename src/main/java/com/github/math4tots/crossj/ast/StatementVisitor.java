package com.github.math4tots.crossj.ast;

public interface StatementVisitor<R, A> {
    R visit(BlockStatement n, A a);
    R visit(ExpressionStatement n, A a);
    R visit(ReturnStatement n, A a);
}
