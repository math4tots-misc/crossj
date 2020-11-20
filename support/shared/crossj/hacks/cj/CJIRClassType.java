package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
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
    public CJIRType substitute(Map<String, CJIRType> map) {
        return new CJIRClassType(definition, args.map(arg -> arg.substitute(map)));
    }

    @Override
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
