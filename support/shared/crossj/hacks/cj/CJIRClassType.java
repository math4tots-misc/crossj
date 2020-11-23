package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Range;
import crossj.base.Str;
import crossj.base.Try;

public final class CJIRClassType implements CJIRType {
    private final CJAstItemDefinition definition;
    private final List<CJIRType> args;

    CJIRClassType(CJAstItemDefinition definition, List<CJIRType> args) {
        Assert.that(!definition.isTrait());
        this.definition = definition;
        this.args = args;
    }

    public CJAstItemDefinition getDefinition() {
        return definition;
    }

    public List<CJIRType> getArguments() {
        return args;
    }

    @Override
    public List<String> getTypeParameterNames() {
        return definition.getTypeParameters().map(p -> p.getName());
    }

    @Override
    public CJIRType substitute(Map<String, CJIRType> map) {
        return new CJIRClassType(definition, args.map(arg -> arg.substitute(map)));
    }

    public Optional<CJIRUnionCaseDescriptor> getUnionCaseDescriptor(String name) {
        var caseDefOption = definition.getUnionCaseDefinitionFor(name);
        if (caseDefOption.isEmpty()) {
            return Optional.empty();
        }
        var caseDef = caseDefOption.get();
        var params = definition.getTypeParameters().map(p -> p.getName());
        var map = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
        var signature = new CJIRMethodSignature(caseDef.getValueTypes().map(t -> t.getAsIsType().substitute(map)), this);
        return Optional.of(new CJIRUnionCaseDescriptor(caseDef.getTag(), caseDef.getName(), signature));
    }

    @Override
    public Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName) {
        var incompleteMethodDescriptor = definition.getMethodMap().getOrNull(methodName);
        if (incompleteMethodDescriptor == null) {
            return Try.fail("Method " + methodName + " not found in " + this);
        } else {
            var params = definition.getTypeParameters().map(p -> p.getName());
            var typeMap = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
            var item = incompleteMethodDescriptor.item;
            var itemTypeArguments = incompleteMethodDescriptor.itemTypeArguments.map(t -> t.substitute(typeMap));
            var method = incompleteMethodDescriptor.method;
            return Try.ok(new CJIRMethodDescriptor(item, itemTypeArguments, this, method));
        }
    }

    public List<CJIRTrait> getReifiedTraits() {
        var params = definition.getTypeParameters().map(p -> p.getName());
        var map = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
        map.put("Self", this);
        return definition.getTraits().map(t -> t.getAsIsTrait().substitute(map));
    }

    @Override
    public String toString() {
        if (args.size() == 0) {
            return definition.getQualifiedName();
        } else {
            return definition.getQualifiedName() + "[" + Str.join(", ", args) + "]";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CJIRClassType)) {
            return false;
        }
        var other = (CJIRClassType) obj;
        return definition == other.definition && args.equals(other.args);
    }
}
