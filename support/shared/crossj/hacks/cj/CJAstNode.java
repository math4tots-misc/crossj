package crossj.hacks.cj;

import crossj.base.Str;
import crossj.base.StrBuilder;

public interface CJAstNode {
    CJMark getMark();

    void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix);

    default void addInspect(StrBuilder sb, int depth) {
        addInspect0(sb, depth, true, "");
    }

    default String inspect() {
        var sb = Str.builder();
        addInspect(sb, 0);
        return sb.build();
    }

    default String inspect0() {
        var sb = Str.builder();
        addInspect0(sb, 0, false, "");
        return Str.strip(sb.build());
    }
}
