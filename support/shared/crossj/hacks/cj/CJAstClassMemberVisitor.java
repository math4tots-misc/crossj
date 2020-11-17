package crossj.hacks.cj;

public interface CJAstClassMemberVisitor<R, A> {
    R visitField(CJAstFieldDefinition m, A a);
    R visitMethod(CJAstMethodDefinition m, A a);
}
