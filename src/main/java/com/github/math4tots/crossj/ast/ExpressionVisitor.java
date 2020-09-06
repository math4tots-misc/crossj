package com.github.math4tots.crossj.ast;

public interface ExpressionVisitor<R, A> {
    R visitFieldAccessExpression(FieldAccessExpression n, A a);
    R visitMethodCallExpression(MethodCallExpression n, A a);
    R visitOperationExpression(OperationExpression n, A a);
    R visitStringLiteralExpression(StringLiteralExpression n, A a);
    R visitIntegerLiteralExpression(IntegerLiteralExpression n, A a);
    R visitDoubleLiteralExpression(DoubleLiteralExpression n, A a);
    R visitNameExpression(NameExpression n, A a);
    R visitTypeCastExpression(TypeCastExpression n, A a);
    R visitInstanceOfExpression(InstanceOfExpression n, A a);
}
