package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;

/**
 * The signature of a method after all type parameters have been substituted.
 */
public final class CJIRMethodSignature {
    public final List<CJIRType> argumentTypes;
    public final CJIRType returnType;

    CJIRMethodSignature(List<CJIRType> argumentTypes, CJIRType returnType) {
        this.argumentTypes = argumentTypes;
        this.returnType = returnType;
    }

    CJIRMethodSignature substitute(Map<String, CJIRType> map) {
        return new CJIRMethodSignature(argumentTypes.map(t -> t.substitute(map)), returnType.substitute(map));
    }

    /**
     * Given: - an item, - a method directly owned by that item, - the type
     * arguments for the item, - the type arguments for the method, computes the
     * resulting method signature
     */
    static CJIRMethodSignature compute(CJAstItemDefinition item, CJAstMethodDefinition method,
            List<CJIRType> itemTypeArguments, CJIRType selfType, List<CJIRType> methodTypeArguments) {
        Assert.equals(item.getTypeParameters().size(), itemTypeArguments.size());
        Assert.equals(method.getTypeParameters().size(), methodTypeArguments.size());
        var binding = Map.<String, CJIRType>of();
        binding.put("Self", selfType);
        for (int i = 0; i < itemTypeArguments.size(); i++) {
            binding.put(item.getTypeParameters().get(i).getName(), itemTypeArguments.get(i));
        }
        for (int i = 0; i < methodTypeArguments.size(); i++) {
            binding.put(method.getTypeParameters().get(i).getName(), methodTypeArguments.get(i));
        }
        return asIsFromMethod(method).substitute(binding);
    }

    static CJIRMethodSignature asIsFromMethod(CJAstMethodDefinition method) {
        return new CJIRMethodSignature(method.getParameters().map(p -> p.getType().getAsIsType()),
                method.getReturnType().getAsIsType());
    }
}
