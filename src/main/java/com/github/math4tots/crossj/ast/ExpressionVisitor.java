package com.github.math4tots.crossj.ast;

public interface ExpressionVisitor<R, A> {
    R visit(FieldAccessExpression n, A a);
    R visit(MethodCallExpression n, A a);
    R visit(OperationExpression n, A a);
    R visit(StringLiteralExpression n, A a);
    R visit(IntegerLiteralExpression n, A a);
    R visit(DoubleLiteralExpression n, A a);
    R visit(NameExpression n, A a);
    R visit(TypeCastExpression n, A a);
    R visit(InstanceOfExpression n, A a);
}
