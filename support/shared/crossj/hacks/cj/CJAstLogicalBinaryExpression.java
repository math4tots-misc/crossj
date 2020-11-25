package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.StrBuilder;

public final class CJAstLogicalBinaryExpression implements CJAstExpression {
    public static final int AND = 1;
    public static final int OR = 2;

    private final CJMark mark;
    private final int op;
    private final CJAstExpression left;
    private final CJAstExpression right;
    CJIRType resolvedType;
    int complexityFlags;

    CJAstLogicalBinaryExpression(CJMark mark, int op, CJAstExpression left, CJAstExpression right) {
        this.mark = mark;
        this.op = op;
        this.left = left;
        this.right = right;
        Assert.that(op == AND || op == OR);
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public boolean isAnd() {
        return op == AND;
    }

    public boolean isOr() {
        return op == OR;
    }

    public CJAstExpression getLeft() {
        return left;
    }

    public CJAstExpression getRight() {
        return right;
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(left.inspect0()).s(op == AND ? " and " : " or ").s(right.inspect0()).s(suffix).s("\n");
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitLogicalBinary(this, a);
    }
}
