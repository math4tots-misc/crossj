package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstLiteralExpression implements CJAstExpression {
    // various literal types
    public static final String STRING = "String";
    public static final String CHAR = "Char";
    public static final String INT = "Int";
    public static final String DOUBLE = "Double";
    public static final List<String> TYPES = List.of(STRING, CHAR, INT, DOUBLE);

    private final CJMark mark;
    private final String type;
    private final String rawText;

    CJAstLiteralExpression(CJMark mark, String type, String rawText) {
        Assert.that(TYPES.contains(type));
        this.mark = mark;
        this.type = type;
        this.rawText = rawText;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getType() {
        return type;
    }

    public String getRawText() {
        return rawText;
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitLiteral(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(rawText).s(suffix).s("\n");
    }
}
