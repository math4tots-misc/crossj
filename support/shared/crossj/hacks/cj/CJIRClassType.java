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
    private final Map<String, Try<CJIRMethodDescriptor>> methodDescriptorCache = Map.of();

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
        if (!methodDescriptorCache.containsKey(methodName)) {
            methodDescriptorCache.put(methodName, getMethodDescriptorUncached(methodName));
        }
        return methodDescriptorCache.get(methodName);
    }

    public Try<CJIRMethodDescriptor> getMethodDescriptorUncached(String methodName) {
        // first search for the method defined directly in this class.
        {
            var optionMember = definition.getMemberDefinitionByName(methodName);
            if (optionMember.isPresent()) {
                var member = optionMember.get();
                if (member instanceof CJAstMethodDefinition) {
                    var method = (CJAstMethodDefinition) member;
                    return Try.ok(new CJIRMethodDescriptor(definition, args, this, method));
                } else {
                    return Try.fail(definition.getQualifiedName() + "." + methodName + " is not a method");
                }
            }
        }

        // find the method in one of the traits
        {
            var params = definition.getTypeParameters().map(p -> p.getName());
            var map = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
            for (var trait : definition.getAllResolvedTraits().iter().map(t -> t.substitute(map))) {
                var optionMember = trait.getDefinition().getMemberDefinitionByName(methodName);
                if (optionMember.isPresent()) {
                    var member = optionMember.get();
                    if (member instanceof CJAstMethodDefinition) {
                        var method = (CJAstMethodDefinition) member;
                        return Try.ok(new CJIRMethodDescriptor(trait.getDefinition(), trait.getArguments(), this, method));
                    } else {
                        return Try.fail(definition.getQualifiedName() + "." + methodName + " (" + trait + ") is not a method");
                    }
                }
            }
        }
        return Try.fail("Method " + methodName + " not found in " + this);
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
