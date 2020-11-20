package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Try;

public final class CJIRTrait {
    private final CJAstItemDefinition definition;
    private final List<CJIRType> args;

    CJIRTrait(CJAstItemDefinition definition, List<CJIRType> args) {
        Assert.that(definition.isTrait());
        this.definition = definition;
        this.args = args;
    }

    public CJAstItemDefinition getDefinition() {
        return definition;
    }

    public List<CJIRType> getArguments() {
        return args;
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
}
