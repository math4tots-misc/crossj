package crossj.hacks.cj;

public interface CJAstItemMemberVisitor<R, A> {
    R visitField(CJAstFieldDefinition m, A a);
    R visitMethod(CJAstMethodDefinition m, A a);
}
