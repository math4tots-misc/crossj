package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Map;
import crossj.base.Try;

public interface CJIRType {

    CJIRType substitute(Map<String, CJIRType> map);

    Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName);

    List<String> getTypeParameterNames();

    boolean isUnion();

    boolean implementsTrait(CJIRTrait trait);

    boolean isFunctionType(int argc);

    boolean isDerivedFrom(CJAstItemDefinition item);
}
