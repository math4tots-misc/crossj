package crossj.hacks.cj;

public final class CJIRUnionCaseDescriptor {
    public final int tag;
    public final String name;
    public final CJIRMethodSignature signature;

    CJIRUnionCaseDescriptor(int tag, String name, CJIRMethodSignature signature) {
        this.tag = tag;
        this.name = name;
        this.signature = signature;
    }
}
