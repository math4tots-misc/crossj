package crossj.hacks.cj;

public interface CJAstExpressionVisitor<R, A> {
    R visitMethodCall(CJAstMethodCallExpression e, A a);
    R visitName(CJAstNameExpression e, A a);
    R visitLiteral(CJAstLiteralExpression e, A a);
    R visitStaticFieldAccess(CJAstStaticFieldAccessExpression e, A a);
    R visitFieldAccess(CJAstFieldAccessExpression e, A a);
    R visitNew(CJAstNewExpression e, A a);
    R visitStaticMethodCall(CJAstStaticMethodCallExpression e, A a);
    R visitInstanceMethodCall(CJAstInstanceMethodCallExpression e, A a);
    R visitEmptyMutableList(CJAstEmptyMutableListExpression e, A a);
    R visitNewUnion(CJAstNewUnionExpression e, A a);
    R visitLambda(CJAstLambdaExpression e, A a);
    R visitListDisplay(CJAstListDisplayExpression e, A a);
    R visitTupleDisplay(CJAstTupleDisplayExpression e, A a);
    R visitLogicalNot(CJAstLogicalNotExpression e, A a);
    R visitLogicalBinary(CJAstLogicalBinaryExpression e, A a);
    R visitConditional(CJAstConditionalExpression e, A a);
    R visitCompound(CJAstCompoundExpression e, A a);
    R visitErrorPropagation(CJAstErrorPropagationExpression e, A a);
}
