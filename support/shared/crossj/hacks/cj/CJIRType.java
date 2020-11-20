package crossj.hacks.cj;

import crossj.base.Map;
import crossj.base.Try;

public interface CJIRType {

    CJIRType substitute(Map<String, CJIRType> map);

    Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName);
}
