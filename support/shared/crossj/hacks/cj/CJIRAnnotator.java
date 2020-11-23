package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Range;
import crossj.base.Set;
import crossj.base.Str;
import crossj.base.Try;
import crossj.base.XError;

public final class CJIRAnnotator
        implements CJAstStatementVisitor<Void, Void>, CJAstExpressionVisitor<Void, Optional<CJIRType>> {

    public static Try<Void> annotate(CJIRWorld world) {
        var context = new CJIRContext(world);
        var annotator = new CJIRAnnotator(context);
        try {
            for (var item : world.getAllItems()) {
                annotator.preAnnotateItem(item);
            }
            for (var item : world.getAllItems()) {
                annotator.computeTraitAndMethodSet(item);
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

    private CJIRClassType getSimpleTypeByQualifiedName(String qualifiedName) {
        var item = context.world.getItem(qualifiedName);
        Assert.equals(item.getTypeParameters().size(), 0);
        return new CJIRClassType(item, List.of());
    }

    private CJIRClassType getMutableListTypeOf(CJIRType innerType) {
        return new CJIRClassType(mutableListDefinition, List.of(innerType));
    }

    private static CJIRAnnotatorException err0(String message, CJMark mark) {
        // throw XError.withMessage("MESSAGE = " + message);
        return CJIRAnnotatorException.fromParts(message, List.of(mark));
    }

    private void annotateTypeExpression(CJAstTypeExpression typeExpression) {
        context.resolveTypeExpression(typeExpression);
    }

    private void enterItem(CJAstItemDefinition item) {
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

    private void exitItem() {
        context.exitItem();
    }

    private void enterMethod(CJAstMethodDefinition method) {
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

    private void exitMethod() {
        context.exitMethod();
    }

    /**
     * Before we get deeper into the annotation, we need to make sure that all the
     * method and field types of the item are properly annotated so that
     * 'getMethodSignature()' can be called on any type safely.
     */
    private void preAnnotateItem(CJAstItemDefinition item) {
        enterItem(item);
        for (var traitExpression : item.getTraits()) {
            context.resolveTraitExpression(traitExpression);
        }
        for (var member : item.getMembers()) {
            int nextUnionCaseTag = 0;
            if (member instanceof CJAstMethodDefinition) {
                var method = (CJAstMethodDefinition) member;
                enterMethod(method);
                for (var arg : method.getParameters()) {
                    annotateTypeExpression(arg.getType());
                }
                annotateTypeExpression(method.getReturnType());
                exitMethod();
            } else if (member instanceof CJAstFieldDefinition) {
                if (item.isUnion() || item.isTrait()) {
                    throw err0("Only non-union classes may have fields", member.getMark());
                }
                var field = (CJAstFieldDefinition) member;
                annotateTypeExpression(field.getType());
            } else if (member instanceof CJAstUnionCaseDefinition) {
                if (!item.isUnion()) {
                    throw err0("Only unions may have case members", member.getMark());
                }
                var ucase = (CJAstUnionCaseDefinition) member;
                ucase.tag = nextUnionCaseTag++;
                for (var typeExpression : ucase.getValueTypes()) {
                    annotateTypeExpression(typeExpression);
                }
            }
        }
        exitItem();
    }

    private void computeTraitAndMethodSet(CJAstItemDefinition item) {
        enterItem(item);

        // ==============================================================================
        // compute trait set
        // ==============================================================================

        // TODO: Detect trait-generic conflicts (i.e. traits that are implemented more
        // than once with different generic arguments) instead of ignoring them
        // silently.
        {
            var seen = Set.of(item.getQualifiedName());
            var allTraits = List.<CJIRTrait>of();
            var stack = List.reversed(item.getTraits().map(t -> t.getAsIsTrait()));
            for (var trait : stack) {
                seen.add(trait.getDefinition().getQualifiedName());
            }
            while (stack.size() > 0) {
                var trait = stack.pop();
                allTraits.add(trait);
                var newTraits = List.reversed(trait.getReifiedTraits().iter()
                        .filter(t -> !seen.contains(t.getDefinition().getQualifiedName())));
                seen.addAll(newTraits.map(t -> t.getDefinition().getQualifiedName()));
                stack.addAll(newTraits);
            }
            item.allResolvedTraits = allTraits;
        }

        // ==============================================================================
        // compute method map
        // ==============================================================================
        // TODO: Detect conflicting method declarations instead of silently ignoring
        // them
        var methodMap = Map.<String, CJIRIncompleteMethodDescriptor>of();
        var itemTypeArgs = item.getTypeParameters().map(t -> (CJIRType) new CJIRVariableType(t, true));
        for (var methodDefinition : item.getMethods()) {
            methodMap.put(methodDefinition.getName(),
                    new CJIRIncompleteMethodDescriptor(item, itemTypeArgs, methodDefinition));
        }
        for (var trait : item.allResolvedTraits) {
            for (var methodDefinition : trait.getDefinition().getMethods()) {
                if (methodMap.containsKey(methodDefinition.getName())) {
                    continue;
                }
                methodMap.put(methodDefinition.getName(), new CJIRIncompleteMethodDescriptor(trait.getDefinition(),
                        trait.getArguments(), methodDefinition));
            }
        }
        item.methodMap = methodMap;

        if (item.isClass()) {
            // ==============================================================================
            // check that all required methods are implemented
            // ==============================================================================
            var selfType = new CJIRClassType(item, itemTypeArgs);
            for (var trait : item.allResolvedTraits) {
                for (var methodDefinition : trait.getDefinition().getMethods().filter(m -> m.getBody().isEmpty())) {
                    if (!methodMap.containsKey(methodDefinition.getName())) {
                        throw err0(selfType + " implements " + trait + " but does not implement method "
                                + methodDefinition.getName(), item.getMark());
                    }
                }
            }
        }

        exitItem();
    }

    private void annotateItem(CJAstItemDefinition item) {
        // TODO: Check for method signature conflicts in the implementing traits.
        enterItem(item);
        for (var member : item.getMembers()) {
            if (member instanceof CJAstMethodDefinition) {
                annotateMethod((CJAstMethodDefinition) member);
            }
        }
        exitItem();
    }

    private void annotateMethod(CJAstMethodDefinition method) {
        enterMethod(method);
        if (method.getBody().isPresent()) {
            annotateStatement(method.getBody().get());
        }
        exitMethod();
    }

    private void annotateStatement(CJAstStatement statement) {
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

    void annotateExpressionWithOptionalType(CJAstExpression expression, Optional<CJIRType> optionalType) {
        if (expression.getResolvedTypeOrNull() == null) {
            expression.accept(this, optionalType);
        }
        if (optionalType.isPresent()) {
            var expected = optionalType.get();
            var actual = expression.getResolvedType();
            if (!expected.equals(actual)) {
                throw err0("Expected expression with type " + expected + " but got " + actual, expression.getMark());
            }
        }
    }

    void annotateExpressionWithType(CJAstExpression expression, CJIRType type) {
        annotateExpressionWithOptionalType(expression, Optional.of(type));
    }

    void annotateExpression(CJAstExpression expression) {
        annotateExpressionWithOptionalType(expression, Optional.empty());
    }

    @Override
    public Void visitInstanceMethodCall(CJAstInstanceMethodCallExpression e, Optional<CJIRType> a) {
        var mark = e.getMark();
        var args = e.getArguments();
        annotateExpression(args.get(0));
        var ownerType = args.get(0).getResolvedType();
        var tryMethodDescriptor = ownerType.getMethodDescriptor(e.getName());
        if (tryMethodDescriptor.isFail()) {
            throw err0(tryMethodDescriptor.getErrorMessageWithContext(), mark);
        }
        var methodDescriptor = tryMethodDescriptor.get();
        var typeArguments = inferTypeArguments(mark, methodDescriptor, args, a);
        e.resolvedType = annotateMethodCall0(mark, methodDescriptor, typeArguments, args);
        e.inferredTypeArguments = typeArguments;
        e.inferredOwnerType = ownerType;
        return null;
    }

    @Override
    public Void visitInferredGenericsMethodCall(CJAstInferredGenericsMethodCallExpression e, Optional<CJIRType> a) {
        var mark = e.getMark();
        var args = e.getArguments();
        var ownerType = context.resolveTypeExpression(e.getOwner());
        var tryMethodDescriptor = ownerType.getMethodDescriptor(e.getName());
        if (tryMethodDescriptor.isFail()) {
            throw err0(tryMethodDescriptor.getErrorMessageWithContext(), mark);
        }
        var methodDescriptor = tryMethodDescriptor.get();
        var typeArguments = inferTypeArguments(mark, methodDescriptor, args, a);
        e.resolvedType = annotateMethodCall0(mark, methodDescriptor, typeArguments, args);
        e.inferredTypeArguments = typeArguments;
        return null;
    }

    @Override
    public Void visitMethodCall(CJAstMethodCallExpression e, Optional<CJIRType> a) {
        for (var typeArg : e.getTypeArguments()) {
            context.resolveTypeExpression(typeArg);
        }
        var ownerType = context.resolveTypeExpression(e.getOwner());
        e.resolvedType = annotateMethodCall1(e.getMark(), ownerType, e.getName(),
                e.getTypeArguments().map(t -> t.getAsIsType()), e.getArguments());
        return null;
    }

    /**
     * Given that we know the reified item type, infer the type arguments for the
     * method.
     */
    private List<CJIRType> inferTypeArguments(CJMark mark, CJIRMethodDescriptor methodDescriptor,
            List<CJAstExpression> args, Optional<CJIRType> expectedReturnType) {
        var method = methodDescriptor.method;

        if (method.getParameters().size() == 0) {
            return List.of();
        }

        var map = Map.<String, CJIRType>of();
        for (int i = 0; i < methodDescriptor.itemTypeArguments.size(); i++) {
            map.put(methodDescriptor.item.getTypeParameters().get(i).getName(),
                    methodDescriptor.itemTypeArguments.get(i));
        }

        return inferTypeArguments2(mark, methodDescriptor.item, methodDescriptor.method.getTypeParameters(),
                methodDescriptor.method.getParameters().map(p -> p.getType().getAsIsType()),
                methodDescriptor.method.getReturnType().getAsIsType(), args, expectedReturnType, Optional.of(map))
                        .get2();
    }

    /**
     * Assuming that we know which item definition to use and unreified signature of
     * the member (for the method, constructor or union case), we try to infer the
     * type arguments.
     *
     * Returns (item-type-arguments, member-type-arguments) pair.
     */
    private Pair<List<CJIRType>, List<CJIRType>> inferTypeArguments2(CJMark mark, CJAstItemDefinition item,
            List<CJAstTypeParameter> memberTypeParameters, List<CJIRType> memberArgTypes, CJIRType memberReturnType,
            List<CJAstExpression> args, Optional<CJIRType> expectedReturnType,
            Optional<Map<String, CJIRType>> partialBindings) {
        if (item.getTypeParameters().size() == 0 && memberTypeParameters.size() == 0) {
            return Pair.of(List.of(), List.of());
        }

        var knownVariables = Set.fromIterable(
                List.of(item.getTypeParameters().map(p -> p.getName()), memberTypeParameters.map(p -> p.getName()))
                        .flatMap(x -> x));
        var map = partialBindings.isPresent() ? partialBindings.get() : Map.<String, CJIRType>of();
        var stack = List.<Pair<CJIRType, CJIRType>>of();
        int nextArgIndex = 0;

        // the first thing to try is making an inference based on the return type, if
        // available
        if (expectedReturnType.isPresent()) {
            stack.add(Pair.of(memberReturnType, expectedReturnType.get()));
        }

        while (map.size() < knownVariables.size() && (stack.size() > 0 || nextArgIndex < args.size())) {
            if (stack.size() == 0) {
                // if the stack is empty, but we have more arguments we can look at, use it.
                var arg = args.get(nextArgIndex);
                annotateExpression(arg);
                stack.add(Pair.of(memberArgTypes.get(nextArgIndex), arg.getResolvedType()));
                nextArgIndex++;
            }
            var pair = stack.pop();
            var param = pair.get1();
            var given = pair.get2();

            if (param instanceof CJIRVariableType) {
                var variableType = (CJIRVariableType) param;
                var variableName = variableType.getDefinition().getName();
                Assert.that(knownVariables.contains(variableName));
                if (!map.containsKey(variableName)) {
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

        if (map.size() < knownVariables.size()) {
            throw err0("Could not infer type arguments", mark);
        }
        var itemTypeArgs = Range.upto(item.getTypeParameters().size())
                .map(i -> map.get(item.getTypeParameters().get(i).getName())).list();
        var memberTypeArgs = Range.upto(memberTypeParameters.size())
                .map(i -> map.get(memberTypeParameters.get(i).getName())).list();
        return Pair.of(itemTypeArgs, memberTypeArgs);
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
            annotateExpressionWithType(arg, argType);
        }
        return methodSignature.returnType;
    }

    @Override
    public Void visitName(CJAstNameExpression e, Optional<CJIRType> a) {
        var type = context.getVariableType(e.getName());
        if (type.isEmpty()) {
            throw err0("Name '" + e.getName() + "' is not defined", e.getMark());
        }
        e.resolvedType = type.get();
        return null;
    }

    @Override
    public Void visitLiteral(CJAstLiteralExpression e, Optional<CJIRType> a) {
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
    public Void visitEmptyMutableList(CJAstEmptyMutableListExpression e, Optional<CJIRType> a) {
        annotateTypeExpression(e.getType());
        e.resolvedType = getMutableListTypeOf(e.getType().getAsIsType());
        return null;
    }

    @Override
    public Void visitLogicalNot(CJAstLogicalNotExpression e, Optional<CJIRType> a) {
        annotateExpressionWithType(e.getInner(), boolType);
        e.resolvedType = boolType;
        return null;
    }

    @Override
    public Void visitNew(CJAstNewExpression e, Optional<CJIRType> a) {
        annotateTypeExpression(e.getType());
        for (var arg : e.getArguments()) {
            annotateExpression(arg);
        }
        var rawType = e.getType().getAsIsType();
        if (rawType instanceof CJIRVariableType) {
            // TODO: Consider whether I want to allow this.
            throw err0("'new' cannot be used with variable types", e.getMark());
        }
        var classType = (CJIRClassType) rawType;
        if (classType.getDefinition().isUnion()) {
            throw err0("'new' cannot be used with union types", e.getMark());
        }
        e.resolvedType = classType;
        return null;
    }

    @Override
    public Void visitNewUnion(CJAstNewUnionExpression e, Optional<CJIRType> a) {

        CJIRClassType classType = null;

        var optionalItem = context.getItem(e.getType().getName());
        if (optionalItem.isPresent() && optionalItem.get().isUnion() && e.getType().getArguments().size() == 0
                && optionalItem.get().getTypeParameters().size() > 0) {
            var item = optionalItem.get();
            // In this case, we have to infer the owner type
            var optionalUnionCase = item.getUnionCaseDefinitionFor(e.getName());
            if (optionalUnionCase.isEmpty()) {
                throw err0("Union constructor " + item.getQualifiedName() + "." + e.getName() + " not found",
                        e.getMark());
            }
            var unionCase = optionalUnionCase.get();
            var inferredItemTypeArgs = inferTypeArguments2(e.getMark(), item, List.of(),
                    unionCase.getValueTypes().map(t -> context.resolveTypeExpression(t)), item.getAsSelfClassType(),
                    e.getArguments(), a, Optional.empty()).get1();
            classType = new CJIRClassType(item, inferredItemTypeArgs);
        } else {
            annotateTypeExpression(e.getType());
            var rawType = e.getType().getAsIsType();
            if (rawType instanceof CJIRVariableType) {
                throw err0("Union constructors cannot be used with variable types", e.getMark());
            }
            classType = (CJIRClassType) rawType;
        }

        if (!classType.getDefinition().isUnion()) {
            throw err0("Union constructors cannot be used with non-union types", e.getMark());
        }
        var optionUnionCaseDescriptor = classType.getUnionCaseDescriptor(e.getName());
        if (optionUnionCaseDescriptor.isEmpty()) {
            throw err0("Union constructor " + classType + "." + e.getName() + " not found", e.getMark());
        }
        var unionCaseDescriptor = optionUnionCaseDescriptor.get();
        for (var arg : e.getArguments()) {
            annotateExpression(arg);
        }
        e.resolvedType = classType;
        e.resolvedUnionCaseDescriptor = unionCaseDescriptor;
        return null;
    }
}
