package crossj.hacks.cj;

public interface CJAstStatementVisitor<R, A> {
    R visitBlock(CJAstBlockStatement s, A a);
    R visitExpression(CJAstExpressionStatement s, A a);
    R visitReturn(CJAstReturnStatement s, A a);
    R visitIf(CJAstIfStatement s, A a);
}
