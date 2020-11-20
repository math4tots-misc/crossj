package crossj.hacks.cj;

import crossj.base.StrBuilder;

public final class CJAstImport implements CJAstNode {
    private final CJMark mark;
    private final String qualifiedName;

    CJAstImport(CJMark mark, String qualifiedName) {
        this.mark = mark;
        this.qualifiedName = qualifiedName;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("import ").s(qualifiedName).s(suffix).s("\n");
    }
}
