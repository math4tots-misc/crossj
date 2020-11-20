package crossj.hacks.cj;

import crossj.base.Tuple;

public final class CJIRMethodSignature {
    public final CJIRType returnType;
    public final Tuple<CJIRType> argumentTypes;

    CJIRMethodSignature(CJIRType returnType, Tuple<CJIRType> argumentTypes) {
        this.returnType = returnType;
        this.argumentTypes = argumentTypes;
    }
}
