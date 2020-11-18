package crossj.hacks.cj;

import crossj.base.Str;
import crossj.base.StrBuilder;

final class CJStrBuilder {
    private final StrBuilder sb;
    private int indentation = 0;

    CJStrBuilder() {
        sb = Str.builder();
    }

    void indent() {
        indentation++;
    }

    void dedent() {
        indentation--;
    }

    void line(String string) {
        sb.repeatStr("  ", indentation).s(string).s("\n");
    }

    void lineStart(String string) {
        sb.repeatStr("  ", indentation).s(string);
    }

    void lineBody(String string) {
        sb.s(string);
    }

    void lineEnd(String string) {
        sb.s(string).s("\n");
    }

    String build() {
        return sb.build();
    }
}
