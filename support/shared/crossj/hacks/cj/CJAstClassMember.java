package crossj.hacks.cj;

public interface CJAstClassMember extends CJAstNode {
    String getName();

    int getModifiers();

    default boolean isStatic() {
        return (getModifiers() & CJAstClassMemberModifiers.STATIC) != 0;
    }

    default boolean isPrivate() {
        return (getModifiers() & CJAstClassMemberModifiers.PRIVATE) != 0;
    }
}
