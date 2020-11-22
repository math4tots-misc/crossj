package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
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

    public Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName) {
        for (var member : definition.getMembers()) {
            if (member.getName().equals(methodName)) {
                if (member instanceof CJAstMethodDefinition) {
                    var method = (CJAstMethodDefinition) member;
                    return Try.ok(new CJIRMethodDescriptor(definition, args, method));
                } else {
                    return Try.fail(definition.getQualifiedName() + "." + methodName + " is not a method");
                }
            }
        }
        return Try.fail("Method " + methodName + " not found in " + definition.getQualifiedName());
    }

    public List<CJIRTrait> getReifiedTraits() {
        var params = definition.getTypeParameters().map(p -> p.getName());
        var map = Map.fromIterable(Range.upto(args.size()).map(i -> Pair.of(params.get(i), args.get(i))));
        return definition.getTraits().map(t -> t.getAsIsTrait().substitute(map));
    }

    @Override
    public String toString() {
        if (args.size() == 0) {
            return definition.getQualifiedName();
        } else {
            return definition.getQualifiedName() + "[" + Str.join(",", args) + "]";
        }
    }
}
