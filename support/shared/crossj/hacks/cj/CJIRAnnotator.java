package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Pair;
import crossj.base.Str;
import crossj.base.Try;
import crossj.base.XError;

public final class CJIRAnnotator implements CJAstStatementVisitor<Void, Void>, CJAstExpressionVisitor<Void, Void> {

    public static Try<Void> annotate(CJIRWorld world) {
        var context = new CJIRContext(world);
        var annotator = new CJIRAnnotator(context);
        try {
            for (var item : world.getAllItems()) {
                annotator.preAnnotateItem(item);
            }
            for (var item : world.getAllItems()) {
                annotator.annotateItem(item);
            }
        } catch (CJIRAnnotatorException exc) {
            // NOTE: required to get it working in the JS backend
            if (!(exc instanceof CJIRAnnotatorException)) {
                throw exc;
            }

            var ret = Try.fail(exc.getMessage());
            for (var mark : exc.getMarks()) {
                ret = ret.withContext(mark.filename + ":" + mark.line + ":" + mark.column);
            }
            return ret.castFail();
        }
        return Try.ok(null);
    }

    private static Pair<String, String> splitQualifiedName(String qualifiedName) {
        var parts = Str.split(qualifiedName, ".");
        return Pair.of(Str.join(".", parts.slice(0, parts.size() - 1)), parts.get(parts.size() - 1));
    }

    private final CJIRContext context;
    private final CJIRClassType unitType;
    private final CJIRClassType boolType;
    private final CJIRClassType intType;
    private final CJIRClassType doubleType;
    private final CJIRClassType stringType;

    private CJIRAnnotator(CJIRContext context) {
        this.context = context;
        this.unitType = getSimpleTypeByQualifiedName("cj.Unit");
        this.boolType = getSimpleTypeByQualifiedName("cj.Bool");
        this.intType = getSimpleTypeByQualifiedName("cj.Int");
        this.doubleType = getSimpleTypeByQualifiedName("cj.Double");
        this.stringType = getSimpleTypeByQualifiedName("cj.String");
    }

    public CJIRClassType getSimpleTypeByQualifiedName(String qualifiedName) {
        var item = context.world.getItem(qualifiedName);
        Assert.equals(item.getTypeParameters().size(), 0);
        return new CJIRClassType(item, List.of());
    }

    public static CJIRAnnotatorException err0(String message, CJMark mark) {
        // throw XError.withMessage("MESSAGE = " + message);
        return CJIRAnnotatorException.fromParts(message, List.of(mark));
    }

    void annotateTypeExpression(CJAstTypeExpression typeExpression) {
        context.resolveTypeExpression(typeExpression);
    }

    void enterItem(CJAstItemDefinition item) {
        context.enterItem(item);
        context.declareImport(item.getShortName(), item.getQualifiedName(), item.getMark());
        if (item.isTrait()) {
            context.declareTypeVariable(item.getSelfTypeParameter());
        } else {
            // In this case, the 'Self' property will be set as the context resolves
            // the type expression.
        }
        for (var imp : item.getImports()) {
            var shortName = splitQualifiedName(imp.getQualifiedName()).get2();
            context.declareImport(shortName, imp.getQualifiedName(), imp.getMark());
        }
        for (var typeParameter : item.getTypeParameters()) {
            context.declareTypeVariable(typeParameter);
        }

        if (item.isTrait()) {
            for (var traitExpression : item.getSelfTypeParameter().getBounds()) {
                context.resolveTraitExpression(traitExpression);
            }
        }
        for (var typeParameter : item.getTypeParameters()) {
            for (var traitExpression : typeParameter.getBounds()) {
                context.resolveTraitExpression(traitExpression);
            }
        }
    }

    void exitItem() {
        context.exitItem();
    }

    void enterMethod(CJAstMethodDefinition method) {
        context.enterMethod(method);
        for (var typeParameter : method.getTypeParameters()) {
            context.declareTypeVariable(typeParameter);
        }
        for (var typeParameter : method.getTypeParameters()) {
            for (var traitExpression : typeParameter.getBounds()) {
                context.resolveTraitExpression(traitExpression);
            }
        }
        for (var parameter : method.getParameters()) {
            context.resolveTypeExpression(parameter.getType());
            context.declareVariable(parameter.getName(), parameter.getType().getAsIsType(), parameter.getMark());
        }
    }

    void exitMethod() {
        context.exitMethod();
    }

    /**
     * Before we get deeper into the annotation, we need to make sure that all the
     * method and field types of the item are properly annotated so that
     * 'getMethodSignature()' can be called on any type safely.
     */
    void preAnnotateItem(CJAstItemDefinition item) {
        enterItem(item);
        for (var member : item.getMembers()) {
            if (member instanceof CJAstMethodDefinition) {
                var method = (CJAstMethodDefinition) member;
                enterMethod(method);
                for (var arg : method.getParameters()) {
                    annotateTypeExpression(arg.getType());
                }
                annotateTypeExpression(method.getReturnType());
                exitMethod();
            } else if (member instanceof CJAstFieldDefinition) {
                var field = (CJAstFieldDefinition) member;
                annotateTypeExpression(field.getType());
            }
        }
        exitItem();
    }

    void annotateItem(CJAstItemDefinition item) {
        enterItem(item);
        for (var member : item.getMembers()) {
            if (member instanceof CJAstMethodDefinition) {
                annotateMethod((CJAstMethodDefinition) member);
            }
        }
        exitItem();
    }

    void annotateMethod(CJAstMethodDefinition method) {
        enterMethod(method);
        if (method.getBody().isPresent()) {
            annotateStatement(method.getBody().get());
        }
        exitMethod();
    }

    void annotateStatement(CJAstStatement statement) {
        statement.accept(this, null);
    }

    @Override
    public Void visitBlock(CJAstBlockStatement s, Void a) {
        context.enterBlock();
        for (var statement : s.getStatements()) {
            annotateStatement(statement);
        }
        context.exitBlock();
        return null;
    }

    @Override
    public Void visitExpression(CJAstExpressionStatement s, Void a) {
        annotateExpression(s.getExpression());
        return null;
    }

    @Override
    public Void visitReturn(CJAstReturnStatement s, Void a) {
        annotateExpression(s.getExpression());
        return null;
    }

    @Override
    public Void visitIf(CJAstIfStatement s, Void a) {
        annotateExpression(s.getCondition());
        annotateStatement(s.getBody());
        if (s.getOther().isPresent()) {
            annotateStatement(s.getOther().get());
        }
        return null;
    }

    @Override
    public Void visitWhile(CJAstWhileStatement s, Void a) {
        annotateExpression(s.getCondition());
        annotateStatement(s.getBody());
        return null;
    }

    @Override
    public Void visitVariableDeclaration(CJAstVariableDeclarationStatement s, Void a) {
        annotateExpression(s.getExpression());
        var expressionType = s.getExpression().getResolvedType();
        context.declareVariable(s.getName(), expressionType, s.getMark());
        return null;
    }

    @Override
    public Void visitAssignment(CJAstAssignmentStatement s, Void a) {
        annotateExpression(s.getExpression());
        return null;
    }

    void annotateExpression(CJAstExpression expression) {
        expression.accept(this, null);
    }

    @Override
    public Void visitMethodCall(CJAstMethodCallExpression e, Void a) {
        var ownerType = context.resolveTypeExpression(e.getOwner());
        var tryMethodDescriptor = ownerType.getMethodDescriptor(e.getName());
        if (tryMethodDescriptor.isFail()) {
            throw err0(tryMethodDescriptor.getErrorMessage(), e.getMark());
        }
        var methodDescriptor = tryMethodDescriptor.get();
        if (methodDescriptor.method.getTypeParameters().size() != e.getTypeArguments().size()) {
            int argc = e.getTypeArguments().size();
            int arge = methodDescriptor.method.getTypeConditions().size();
            if (argc != arge) {
                throw err0("Expected " + arge + " type args but got " + argc, e.getMark());
            }
        }
        if (methodDescriptor.method.getParameters().size() != e.getArguments().size()) {
            int argc = e.getArguments().size();
            int arge = methodDescriptor.method.getParameters().size();
            if (argc != arge) {
                throw err0("Expected " + arge + " args but got " + argc, e.getMark());
            }
        }
        for (var typeArg : e.getTypeArguments()) {
            context.resolveTypeExpression(typeArg);
        }
        var methodSignature = methodDescriptor.reify(e.getTypeArguments().map(t -> t.getAsIsType()));
        for (int i = 0; i < e.getArguments().size(); i++) {
            var arg = e.getArguments().get(i);
            var argType = methodSignature.argumentTypes.get(i);
            annotateExpression(arg);
            if (!arg.getResolvedType().equals(argType)) {
                throw err0("Expected " + argType + " expression but got " + arg.getResolvedType(), arg.getMark());
            }
        }
        for (var arg : e.getArguments()) {
            annotateExpression(arg);
        }
        e.resolvedType = methodSignature.returnType;
        return null;
    }

    @Override
    public Void visitName(CJAstNameExpression e, Void a) {
        var type = context.getVariableType(e.getName());
        if (type.isEmpty()) {
            throw err0("Name '" + e.getName() + "' is not defined", e.getMark());
        }
        e.resolvedType = type.get();
        return null;
    }

    @Override
    public Void visitLiteral(CJAstLiteralExpression e, Void a) {
        CJIRType type = null;
        if (e.getType().equals("Unit")) {
            type = unitType;
        } else if (e.getType().equals("Bool")) {
            type = boolType;
        } else if (e.getType().equals("Int")) {
            type = intType;
        } else if (e.getType().equals("Double")) {
            type = doubleType;
        } else if (e.getType().equals("String")) {
            type = stringType;
        } else {
            throw XError.withMessage("Unrecognized literal type: " + e.getType());
        }
        e.resolvedType = type;
        return null;
    }

    @Override
    public Void visitNew(CJAstNewExpression e, Void a) {
        for (var arg : e.getArguments()) {
            annotateExpression(arg);
        }
        return null;
    }
}
