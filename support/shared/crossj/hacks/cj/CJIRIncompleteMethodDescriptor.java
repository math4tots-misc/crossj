package crossj.hacks.cj;

import crossj.base.List;

/**
 * Like CJIRMethodDescriptor but missing a self type.
 */
public final class CJIRIncompleteMethodDescriptor {
    public final CJAstItemDefinition item;
    public final List<CJIRType> itemTypeArguments;
    public final CJAstMethodDefinition method;

    CJIRIncompleteMethodDescriptor(CJAstItemDefinition item, List<CJIRType> itemTypeArguments,
            CJAstMethodDefinition method) {
        this.item = item;
        this.itemTypeArguments = itemTypeArguments;
        this.method = method;
    }

    public CJIRMethodDescriptor complete(CJIRType selfType) {
        return new CJIRMethodDescriptor(item, itemTypeArguments, selfType, method);
    }

    /**
     * Returns true iff either
     * - the body of the method is non-empty, or
     * - the method is declared inside a native item
     */
    public boolean isImplemented() {
        return method.getBody().isPresent() || item.isNative();
    }
}
