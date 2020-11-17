package crossj.hacks.cj;

public interface CJAstExpressionVisitor<R, A> {
    R visitMethodCall(CJAstMethodCallExpression e, A a);
    R visitName(CJAstNameExpression e, A a);
    R visitLiteral(CJAstLiteralExpression e, A a);
}
