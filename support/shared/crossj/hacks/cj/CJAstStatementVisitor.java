package crossj.hacks.cj;

public interface CJAstStatementVisitor<R, A> {
    R visitBlock(CJAstBlockStatement s, A a);
    R visitExpression(CJAstExpressionStatement s, A a);
    R visitReturn(CJAstReturnStatement s, A a);
    R visitIf(CJAstIfStatement s, A a);
    R visitWhile(CJAstWhileStatement s, A a);
    R visitFor(CJAstForStatement s, A a);
    R visitVariableDeclaration(CJAstVariableDeclarationStatement s, A a);
    R visitAssignment(CJAstAssignmentStatement s, A a);
    R visitUnionSwitch(CJAstUnionSwitchStatement s, A a);
}
