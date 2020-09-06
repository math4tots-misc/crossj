package com.github.math4tots.crossj.ast;

public interface StatementVisitor<R, A> {
    R visitBlockStatement(BlockStatement n, A a);
    R visitExpressionStatement(ExpressionStatement n, A a);
    R visitReturnStatement(ReturnStatement n, A a);
}
