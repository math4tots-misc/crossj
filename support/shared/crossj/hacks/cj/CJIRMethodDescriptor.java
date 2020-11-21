package crossj.hacks.cj;

import crossj.base.List;

/**
 * A method together with the reified type that it's defined in.
 *
 * The reason for this class is that, for many operations just the method
 * definition is not enough. You also need the item that it's defined in.
 */
public final class CJIRMethodDescriptor {
    public final CJAstItemDefinition item;
    public final List<CJIRType> itemTypeArguments;
    public final CJAstMethodDefinition method;

    CJIRMethodDescriptor(CJAstItemDefinition item, List<CJIRType> itemTypeArguments, CJAstMethodDefinition method) {
        this.item = item;
        this.itemTypeArguments = itemTypeArguments;
        this.method = method;
    }

    public CJIRMethodSignature reify(List<CJIRType> methodTypeArguments) {
        return CJIRMethodSignature.compute(item, method, itemTypeArguments, methodTypeArguments);
    }

    @Override
    public String toString() {
        return item.getQualifiedName() + "." + method.getName();
    }
}
