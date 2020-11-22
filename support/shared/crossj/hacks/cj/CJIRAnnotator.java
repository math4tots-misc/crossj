package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Pair;
import crossj.base.Range;
import crossj.base.Set;
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
    private final CJAstItemDefinition mutableListDefinition;

    private CJIRAnnotator(CJIRContext context) {
        this.context = context;
        this.unitType = getSimpleTypeByQualifiedName("cj.Unit");
        this.boolType = getSimpleTypeByQualifiedName("cj.Bool");
        this.intType = getSimpleTypeByQualifiedName("cj.Int");
        this.doubleType = getSimpleTypeByQualifiedName("cj.Double");
        this.stringType = getSimpleTypeByQualifiedName("cj.String");
        this.mutableListDefinition = context.world.getItem("cj.MutableList");
    }

    public CJIRClassType getSimpleTypeByQualifiedName(String qualifiedName) {
        var item = context.world.getItem(qualifiedName);
        Assert.equals(item.getTypeParameters().size(), 0);
        return new CJIRClassType(item, List.of());
    }

    private CJIRClassType getMutableListTypeOf(CJIRType innerType) {
        return new CJIRClassType(mutableListDefinition, List.of(innerType));
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
        for (var traitExpression : item.getTraits()) {
            context.resolveTraitExpression(traitExpression);
        }
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
        // TODO: Check for method signature conflicts in the implementing traits.
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
        if (!s.getCondition().getResolvedType().equals(boolType)) {
            throw err0("Expected bool but got " + s.getCondition().getResolvedType(), s.getMark());
        }
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
        if (expression.getResolvedTypeOrNull() == null) {
            expression.accept(this, null);
        }
    }

    @Override
    public Void visitInstanceMethodCall(CJAstInstanceMethodCallExpression e, Void a) {
        var mark = e.getMark();
        var args = e.getArguments();
        annotateExpression(args.get(0));
        var ownerType = args.get(0).getResolvedType();
        var tryMethodDescriptor = ownerType.getMethodDescriptor(e.getName());
        if (tryMethodDescriptor.isFail()) {
            throw err0(tryMethodDescriptor.getErrorMessageWithContext(), mark);
        }
        var methodDescriptor = tryMethodDescriptor.get();
        var typeArguments = inferTypeArguments(mark, methodDescriptor, args);
        e.resolvedType = annotateMethodCall0(mark, methodDescriptor, typeArguments, args);
        e.inferredTypeArguments = typeArguments;
        e.inferredOwnerType = ownerType;
        return null;
    }

    @Override
    public Void visitInferredGenericsMethodCall(CJAstInferredGenericsMethodCallExpression e, Void a) {
        var mark = e.getMark();
        var args = e.getArguments();
        var ownerType = context.resolveTypeExpression(e.getOwner());
        var tryMethodDescriptor = ownerType.getMethodDescriptor(e.getName());
        if (tryMethodDescriptor.isFail()) {
            throw err0(tryMethodDescriptor.getErrorMessageWithContext(), mark);
        }
        var methodDescriptor = tryMethodDescriptor.get();
        var typeArguments = inferTypeArguments(mark, methodDescriptor, args);
        e.resolvedType = annotateMethodCall0(mark, methodDescriptor, typeArguments, args);
        e.inferredTypeArguments = typeArguments;
        return null;
    }

    @Override
    public Void visitMethodCall(CJAstMethodCallExpression e, Void a) {
        for (var typeArg : e.getTypeArguments()) {
            context.resolveTypeExpression(typeArg);
        }
        var ownerType = context.resolveTypeExpression(e.getOwner());
        e.resolvedType = annotateMethodCall1(e.getMark(), ownerType, e.getName(),
                e.getTypeArguments().map(t -> t.getAsIsType()), e.getArguments());
        return null;
    }

    private List<CJIRType> inferTypeArguments(CJMark mark, CJIRMethodDescriptor methodDescriptor,
            List<CJAstExpression> args) {
        var method = methodDescriptor.method;
        var parameterTypes = method.getParameters().map(p -> p.getType().getAsIsType());

        if (parameterTypes.size() == 0) {
            return List.of();
        }

        var map = Map.<String, CJIRType>of();
        var unboundVariables = Set.fromIterable(method.getTypeParameters().map(p -> p.getName()));

        // use the arguments from left to right to deduce all the type arguments.
        int nextArgIndex = 0;
        while (map.size() < unboundVariables.size() && nextArgIndex < args.size()) {
            var arg = args.get(nextArgIndex);
            var paramType = parameterTypes.get(nextArgIndex);
            nextArgIndex++;

            annotateExpression(arg);
            var stack = List.of(Pair.of(paramType, arg.getResolvedType()));
            while (map.size() < unboundVariables.size() && stack.size() > 0) {
                var pair = stack.pop();
                var param = pair.get1();
                var given = pair.get2();

                if (param instanceof CJIRVariableType) {
                    var variableType = (CJIRVariableType) param;
                    var variableName = variableType.getDefinition().getName();
                    if (unboundVariables.contains(variableName) && !map.containsKey(variableName)) {
                        // we've encountered an unbound variable. we should bind it.
                        map.put(variableName, given);
                    }
                    // if the variable isn't free, for proper unification we would check that
                    // the concrete types match. However, we silently ignore these cases
                    // because if the call is actually bad, it should be caught later down
                    // the line.
                    // TODO: Throw a 'could not infer types' error when concrete types don't unify.
                } else if (param instanceof CJIRClassType) {
                    var classTypeParam = (CJIRClassType) param;
                    var paramDef = classTypeParam.getDefinition();
                    if (given instanceof CJIRClassType) {
                        var classTypeGiven = (CJIRClassType) given;
                        var givenDef = classTypeGiven.getDefinition();
                        if (paramDef.getQualifiedName().equals(givenDef.getQualifiedName())
                                && classTypeParam.getArguments().size() == classTypeGiven.getArguments().size()) {
                            // the outer part of a reified class type matches.
                            // this means we can proceed to unify the inner parts.
                            for (int i = 0; i < classTypeParam.getArguments().size(); i++) {
                                stack.add(Pair.of(classTypeParam.getArguments().get(i),
                                        classTypeGiven.getArguments().get(i)));
                            }
                        }
                    }
                }
            }
        }
        if (map.size() < unboundVariables.size()) {
            throw err0("Could not infer type arguments", mark);
        }
        return Range.upto(method.getTypeParameters().size())
                .map(i -> map.get(method.getTypeParameters().get(i).getName())).list();
    }

    // convenience: retrieve method descriptor
    private CJIRType annotateMethodCall1(CJMark mark, CJIRType ownerType, String methodName,
            List<CJIRType> typeArguments, List<CJAstExpression> args) {
        var tryMethodDescriptor = ownerType.getMethodDescriptor(methodName);
        if (tryMethodDescriptor.isFail()) {
            throw err0(tryMethodDescriptor.getErrorMessage(), mark);
        }
        var methodDescriptor = tryMethodDescriptor.get();
        return annotateMethodCall0(mark, methodDescriptor, typeArguments, args);
    }

    // all method call types should all ultimately end up here
    private CJIRType annotateMethodCall0(CJMark mark, CJIRMethodDescriptor methodDescriptor,
            List<CJIRType> typeArguments, List<CJAstExpression> args) {
        if (methodDescriptor.method.getTypeParameters().size() != typeArguments.size()) {
            int argc = typeArguments.size();
            int arge = methodDescriptor.method.getTypeConditions().size();
            if (argc != arge) {
                throw err0(methodDescriptor.toString() + " expects " + arge + " type arguments but got " + argc, mark);
            }
        }
        if (methodDescriptor.method.getParameters().size() != args.size()) {
            int argc = args.size();
            int arge = methodDescriptor.method.getParameters().size();
            if (argc != arge) {
                throw err0(methodDescriptor.toString() + " expects " + arge + " arguments but got " + argc, mark);
            }
        }
        var methodSignature = methodDescriptor.reify(typeArguments);
        for (int i = 0; i < args.size(); i++) {
            var arg = args.get(i);
            var argType = methodSignature.argumentTypes.get(i);
            annotateExpression(arg);
            if (!arg.getResolvedType().equals(argType)) {
                throw err0("Expected " + argType + " expression but got " + arg.getResolvedType(), arg.getMark());
            }
        }
        return methodSignature.returnType;
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
    public Void visitEmptyMutableList(CJAstEmptyMutableListExpression e, Void a) {
        annotateTypeExpression(e.getType());
        e.resolvedType = getMutableListTypeOf(e.getType().getAsIsType());
        return null;
    }

    @Override
    public Void visitLogicalNot(CJAstLogicalNotExpression e, Void a) {
        annotateExpression(e.getInner());
        if (!e.getInner().getResolvedType().equals(boolType)) {
            throw err0("Expected bool but got " + e.getInner().getResolvedType(), e.getMark());
        }
        e.resolvedType = boolType;
        return null;
    }

    @Override
    public Void visitNew(CJAstNewExpression e, Void a) {
        annotateTypeExpression(e.getType());
        for (var arg : e.getArguments()) {
            annotateExpression(arg);
        }
        var type = e.getType().getAsIsType();
        if (type instanceof CJIRVariableType) {
            // TODO: Consider whether I want to allow this.
            throw err0("'new' cannot be used with variable types", e.getMark());
        }
        e.resolvedType = type;
        return null;
    }
}
