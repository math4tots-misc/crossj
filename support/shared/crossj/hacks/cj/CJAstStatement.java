package crossj.hacks.cj;

public interface CJAstStatement extends CJAstNode {
    <R, A> R accept(CJAstStatementVisitor<R, A> visitor, A a);
}
