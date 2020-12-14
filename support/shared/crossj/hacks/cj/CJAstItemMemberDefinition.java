package crossj.hacks.cj;

import crossj.base.Optional;

public interface CJAstItemMemberDefinition extends CJAstNode {
    String getName();

    int getModifiers();

    Optional<String> getComment();

    default boolean isStatic() {
        return (getModifiers() & CJAstItemMemberModifiers.STATIC) != 0;
    }

    default boolean isPrivate() {
        return (getModifiers() & CJAstItemMemberModifiers.PRIVATE) != 0;
    }

    default boolean isAsync() {
        return (getModifiers() & CJAstItemMemberModifiers.ASYNC) != 0;
    }

    <R, A> R accept(CJAstItemMemberVisitor<R, A> visitor, A a);
}
