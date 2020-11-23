package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Range;
import crossj.base.Str;
import crossj.base.Try;

public final class CJIRTrait {
    private final CJAstItemDefinition definition;
    private final List<CJIRType> args;

    CJIRTrait(CJAstItemDefinition definition, List<CJIRType> args) {
        Assert.that(definition.isTrait());
        Assert.equals(definition.getTypeParameters().size(), args.size());
        this.definition = definition;
        this.args = args;
    }

    public CJAstItemDefinition getDefinition() {
        return definition;
    }

    public List<CJIRType> getArguments() {
        return args;
    }

    public String getQualifiedName() {
        return definition.getQualifiedName();
    }

    public CJIRTrait substitute(Map<String, CJIRType> map) {
        return new CJIRTrait(definition, args.map(arg -> arg.substitute(map)));
    }

    private Map<String, CJIRType> getBindings() {
        var params = definition.getTypeParameters().map(p -> p.getName());
        var map = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
        return map;
    }

    Try<Optional<CJIRMethodDescriptor>> getMethodDescriptor(String methodName, CJIRType selfType) {
        var incompleteMethodDescriptor = definition.getMethodMap().getOrNull(methodName);
        if (incompleteMethodDescriptor == null) {
            return Try.ok(Optional.empty());
        } else {
            var params = definition.getTypeParameters().map(p -> p.getName());
            var typeMap = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
            var item = incompleteMethodDescriptor.item;
            var itemTypeArguments = incompleteMethodDescriptor.itemTypeArguments.map(t -> t.substitute(typeMap));
            var method = incompleteMethodDescriptor.method;
            return Try.ok(Optional.of(new CJIRMethodDescriptor(item, itemTypeArguments, selfType, method)));
        }
    }

    public List<CJIRTrait> getReifiedTraits() {
        var params = definition.getTypeParameters().map(p -> p.getName());
        var map = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
        return definition.getTraits().map(t -> t.getAsIsTrait().substitute(map));
    }

    public boolean implementsTrait(CJIRTrait trait) {
        if (equals(trait)) {
            return true;
        }
        var implTrait = definition.getTraitsByQualifiedName().getOrNull(trait.getDefinition().getQualifiedName());
        if (implTrait == null) {
            return false;
        }
        var reifiedImplTrait = implTrait.substitute(getBindings());
        return trait.equals(reifiedImplTrait);
    }

    @Override
    public String toString() {
        if (args.size() == 0) {
            return definition.getQualifiedName();
        } else {
            return definition.getQualifiedName() + "[" + Str.join(",", args) + "]";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CJIRTrait)) {
            return false;
        }
        var other = (CJIRTrait) obj;
        return definition == other.definition && args.equals(other.args);
    }
}
