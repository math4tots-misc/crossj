package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Str;
import crossj.base.XError;

final class CJJSSimpleExpressionTranslator implements CJAstExpressionVisitor<String, Void> {

    /**
     * Check to see if this expression can be translated with a JSSimpleExpressionTranslator
     */
    public static boolean isSimple(CJAstExpression expression) {
        return (expression.getComplexityFlags() & ~(CJIRExpressionComplexityFlags.NONE|CJIRExpressionComplexityFlags.SIMPLE_LAMBDA)) == 0;
    }

    private final CJJSTypeTranslator typeTranslator;

    CJJSSimpleExpressionTranslator(CJJSTypeTranslator typeTranslator) {
        this.typeTranslator = typeTranslator;
    }

    public String translateExpression(CJAstExpression expression) {
        return expression.accept(this, null);
    }

    @Override
    public String visitInstanceMethodCall(CJAstInstanceMethodCallExpression e, Void a) {
        var owner = e.getInferredOwnerType();
        var methodName = e.getName();
        var typeArguments = e.getInferredTypeArguments();
        var args = e.getArguments();
        return translateMethodCall(owner, methodName, typeArguments, args);
    }

    @Override
    public String visitInferredGenericsMethodCall(CJAstInferredGenericsMethodCallExpression e, Void a) {
        var owner = e.getOwner().getAsIsType();
        var methodName = e.getName();
        var typeArguments = e.getInferredTypeArguments();
        var args = e.getArguments();
        return translateMethodCall(owner, methodName, typeArguments, args);
    }

    @Override
    public String visitMethodCall(CJAstMethodCallExpression e, Void a) {
        var owner = e.getOwner().getAsIsType();
        var methodName = e.getName();
        var typeArguments = e.getTypeArguments().map(t -> t.getAsIsType());
        var args = e.getArguments();
        return translateMethodCall(owner, methodName, typeArguments, args);
    }

    @Override
    public String visitName(CJAstNameExpression e, Void a) {
        // For now, names always refer to local variables.
        // In the future, there's also a chance that we may have
        // global field variables here.
        return CJJSTranslator.nameToLocalVariableName(e.getName());
    }

    @Override
    public String visitLiteral(CJAstLiteralExpression e, Void a) {
        if (e.getType().equals(CJAstLiteralExpression.STRING)) {
            return e.getRawText();
        } else if (e.getType().equals(CJAstLiteralExpression.CHAR)) {
            return e.getRawText();
        } else if (e.getType().equals(CJAstLiteralExpression.INT)) {
            return e.getRawText();
        } else if (e.getType().equals(CJAstLiteralExpression.DOUBLE)) {
            return e.getRawText();
        } else if (e.getType().equals(CJAstLiteralExpression.BOOL)) {
            return e.getRawText();
        } else {
            throw XError.withMessage("Unrecognized literal type: " + e.getType());
        }
    }

    @Override
    public String visitEmptyMutableList(CJAstEmptyMutableListExpression e, Void a) {
        return "[]";
    }

    @Override
    public String visitListDisplay(CJAstListDisplayExpression e, Void a) {
        var sb = Str.builder();
        sb.s("[").s(Str.join(",", e.getElements().map(el -> translateExpression(el)))).s("]");
        return sb.build();
    }

    @Override
    public String visitLambda(CJAstLambdaExpression e, Void a) {
        var sb = Str.builder();
        sb.s("((").s(Str.join(",", e.getParameterNames().map(n -> CJJSTranslator.nameToLocalVariableName(n))))
                .s(") => ").s(translateExpression(e.getReturnExpression())).s(")");
        return sb.build();
    }

    @Override
    public String visitLogicalNot(CJAstLogicalNotExpression e, Void a) {
        return "(!" + translateExpression(e.getInner()) + ")";
    }

    @Override
    public String visitNew(CJAstNewExpression e, Void a) {
        var sb = Str.builder();
        var type = (CJIRClassType) e.getType().getAsIsType();
        var constructorName = CJJSTranslator.qualifiedNameToConstructorName(type.getDefinition().getQualifiedName());
        var args = e.getArguments();
        sb.s(constructorName).s("(");
        if (args.size() > 0) {
            sb.s(translateExpression(args.get(0)));
            for (int i = 1; i < args.size(); i++) {
                sb.s(",").s(translateExpression(args.get(i)));
            }
        }
        sb.s(")");
        return sb.build();
    }

    @Override
    public String visitNewUnion(CJAstNewUnionExpression e, Void a) {
        var sb = Str.builder();
        var unionCaseDescriptor = e.getResolvedUnionCaseDescriptor();
        sb.s("[").i(unionCaseDescriptor.tag);
        for (var arg : e.getArguments()) {
            sb.s(",").s(translateExpression(arg));
        }
        sb.s("]");
        return sb.build();
    }

    private String translateType(CJIRType type) {
        return typeTranslator.translateType(type);
    }

    private String translateMethodCall(CJIRType owner, String methodName, List<CJIRType> typeArguments,
            List<CJAstExpression> args) {
        var sb = Str.builder();
        sb.s(translateType(owner)).s(".").s(CJJSTranslator.nameToMethodName(methodName)).s("(");
        {
            boolean first = true;
            for (var typeArg : typeArguments) {
                if (!first) {
                    sb.s(",");
                }
                first = false;
                sb.s(translateType(typeArg));
            }
            for (var arg : args) {
                if (!first) {
                    sb.s(",");
                }
                first = false;
                sb.s(translateExpression(arg));
            }
        }
        sb.s(")");
        return sb.build();
    }
}