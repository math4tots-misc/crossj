package crossj.hacks.cj;

public interface CJAstClassMemberDefinition extends CJAstNode {
    String getName();

    int getModifiers();

    default boolean isStatic() {
        return (getModifiers() & CJAstClassMemberModifiers.STATIC) != 0;
    }

    default boolean isPrivate() {
        return (getModifiers() & CJAstClassMemberModifiers.PRIVATE) != 0;
    }

    <R, A> R accept(CJAstClassMemberVisitor<R, A> visitor, A a);
}
