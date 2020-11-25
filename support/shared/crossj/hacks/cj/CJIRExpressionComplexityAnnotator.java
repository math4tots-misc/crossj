package crossj.hacks.cj;

import crossj.base.List;

public final class CJIRExpressionComplexityAnnotator implements CJAstExpressionVisitor<Void, Void> {
    private static final int NONE = CJIRExpressionComplexityFlags.NONE;
    private static final int SIMPLE_LAMBDA = CJIRExpressionComplexityFlags.SIMPLE_LAMBDA;
    private static final int COMPLEX_LAMBDA = CJIRExpressionComplexityFlags.COMPLEX_LAMBDA;
    private static final CJIRExpressionComplexityAnnotator instance = new CJIRExpressionComplexityAnnotator();

    private CJIRExpressionComplexityAnnotator() {}

    public static int annotate(CJAstExpression expression) {
        if (expression.getComplexityFlagsOrZero() == 0) {
            expression.accept(instance, null);
        }
        return expression.getComplexityFlags();
    }

    private int annotateList(List<CJAstExpression> expressions) {
        int flags = NONE;
        for (var expression : expressions) {
            flags |= annotate(expression);
        }
        return flags;
    }

    @Override
    public Void visitMethodCall(CJAstMethodCallExpression e, Void a) {
        e.complexityFlags = annotateList(e.getArguments());
        return null;
    }

    @Override
    public Void visitName(CJAstNameExpression e, Void a) {
        e.complexityFlags = NONE;
        return null;
    }

    @Override
    public Void visitLiteral(CJAstLiteralExpression e, Void a) {
        e.complexityFlags = NONE;
        return null;
    }

    @Override
    public Void visitNew(CJAstNewExpression e, Void a) {
        e.complexityFlags = annotateList(e.getArguments());
        return null;
    }

    @Override
    public Void visitInferredGenericsMethodCall(CJAstInferredGenericsMethodCallExpression e, Void a) {
        e.complexityFlags = annotateList(e.getArguments());
        return null;
    }

    @Override
    public Void visitInstanceMethodCall(CJAstInstanceMethodCallExpression e, Void a) {
        e.complexityFlags = annotateList(e.getArguments());
        return null;
    }

    @Override
    public Void visitLogicalNot(CJAstLogicalNotExpression e, Void a) {
        e.complexityFlags = annotate(e.getInner());
        return null;
    }

    @Override
    public Void visitLogicalBinary(CJAstLogicalBinaryExpression e, Void a) {
        e.complexityFlags = annotate(e.getLeft()) | annotate(e.getRight());
        return null;
    }

    @Override
    public Void visitEmptyMutableList(CJAstEmptyMutableListExpression e, Void a) {
        e.complexityFlags = NONE;
        return null;
    }

    @Override
    public Void visitNewUnion(CJAstNewUnionExpression e, Void a) {
        e.complexityFlags = annotateList(e.getArguments());
        return null;
    }

    @Override
    public Void visitLambda(CJAstLambdaExpression e, Void a) {
        var body = e.getBody();
        if (body instanceof CJAstReturnStatement && (annotate(((CJAstReturnStatement) body).getExpression()) & ~(NONE|SIMPLE_LAMBDA)) == 0) {
            e.complexityFlags = NONE | SIMPLE_LAMBDA;
        } else {
            e.complexityFlags = NONE | COMPLEX_LAMBDA;
        }
        return null;
    }

    @Override
    public Void visitListDisplay(CJAstListDisplayExpression e, Void a) {
        e.complexityFlags = annotateList(e.getElements());
        return null;
    }
}
