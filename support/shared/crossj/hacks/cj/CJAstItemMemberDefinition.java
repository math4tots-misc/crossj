package crossj.hacks.cj;

public interface CJAstItemMemberDefinition extends CJAstNode {
    String getName();

    int getModifiers();

    default boolean isStatic() {
        return (getModifiers() & CJAstItemMemberModifiers.STATIC) != 0;
    }

    default boolean isPrivate() {
        return (getModifiers() & CJAstItemMemberModifiers.PRIVATE) != 0;
    }

    <R, A> R accept(CJAstItemMemberVisitor<R, A> visitor, A a);
}
