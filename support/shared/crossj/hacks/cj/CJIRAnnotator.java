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
    private final CJIRClassType charType;
    private final CJIRClassType stringType;
    private final CJAstItemDefinition listDefinition;
    private final CJAstItemDefinition mutableListDefinition;
    private final CJAstItemDefinition iterableDefinition;
    private final CJAstItemDefinition fn0Definition;
    private final CJAstItemDefinition fn1Definition;
    private final CJAstItemDefinition fn2Definition;
    private final CJAstItemDefinition fn3Definition;
    private final CJAstItemDefinition fn4Definition;
    private final CJAstItemDefinition tuple2Definition;
    private final CJAstItemDefinition tuple3Definition;
    private final CJAstItemDefinition tuple4Definition;

    private CJIRAnnotator(CJIRContext context) {
        this.context = context;
        this.unitType = getSimpleTypeByQualifiedName("cj.Unit");
        this.boolType = getSimpleTypeByQualifiedName("cj.Bool");
        this.intType = getSimpleTypeByQualifiedName("cj.Int");
        this.doubleType = getSimpleTypeByQualifiedName("cj.Double");
        this.charType = getSimpleTypeByQualifiedName("cj.Char");
        this.stringType = getSimpleTypeByQualifiedName("cj.String");
        this.listDefinition = context.world.getItem("cj.List");
        this.mutableListDefinition = context.world.getItem("cj.MutableList");
        this.iterableDefinition = context.world.getItem("cj.Iterable");
        this.fn0Definition = context.world.getItem("cj.Fn0");
        this.fn1Definition = context.world.getItem("cj.Fn1");
        this.fn2Definition = context.world.getItem("cj.Fn2");
        this.fn3Definition = context.world.getItem("cj.Fn3");
        this.fn4Definition = context.world.getItem("cj.Fn4");
        this.tuple2Definition = context.world.getItem("cj.Tuple2");
        this.tuple3Definition = context.world.getItem("cj.Tuple3");
        this.tuple4Definition = context.world.getItem("cj.Tuple4");
    }

    private CJIRClassType getSimpleTypeByQualifiedName(String qualifiedName) {
        var item = context.world.getItem(qualifiedName);
        Assert.equals(item.getTypeParameters().size(), 0);
        return new CJIRClassType(item, List.of());
    }

    private CJIRClassType getMutableListTypeOf(CJIRType innerType) {
        return new CJIRClassType(mutableListDefinition, List.of(innerType));
    }

    private CJIRClassType getListTypeOf(CJIRType innerType) {
        return new CJIRClassType(listDefinition, List.of(innerType));
    }

    private CJIRClassType getTupleTypeOf(CJMark mark, List<CJIRType> types) {
        switch (types.size()) {
            case 2:
                return getTuple2TypeOf(types.get(0), types.get(1));
            case 3:
                return getTuple3TypeOf(types.get(0), types.get(1), types.get(2));
            case 4:
                return getTuple4TypeOf(types.get(0), types.get(1), types.get(2), types.get(3));
            default:
                throw err0("Tuple types must have 2, 3, or 4 arguments, but got " + types.size(), mark);
        }
    }

    private CJIRClassType getTuple2TypeOf(CJIRType a0, CJIRType a1) {
        return new CJIRClassType(tuple2Definition, List.of(a0, a1));
    }

    private CJIRClassType getTuple3TypeOf(CJIRType a0, CJIRType a1, CJIRType a2) {
        return new CJIRClassType(tuple3Definition, List.of(a0, a1, a2));
    }

    private CJIRClassType getTuple4TypeOf(CJIRType a0, CJIRType a1, CJIRType a2, CJIRType a3) {
        return new CJIRClassType(tuple4Definition, List.of(a0, a1, a2, a3));
    }

    private CJIRClassType getFunctionType(CJMark mark, CJIRType returnType, List<CJIRType> argumentTypes) {
        switch (argumentTypes.size()) {
            case 0:
                return new CJIRClassType(fn0Definition, List.of(returnType));
            case 1:
                return new CJIRClassType(fn1Definition, List.of(returnType, argumentTypes.get(0)));
            case 2:
                return new CJIRClassType(fn2Definition,
                        List.of(returnType, argumentTypes.get(0), argumentTypes.get(1)));
            case 3:
                return new CJIRClassType(fn3Definition,
                        List.of(returnType, argumentTypes.get(0), argumentTypes.get(1), argumentTypes.get(2)));
            case 4:
                return new CJIRClassType(fn4Definition, List.of(returnType, argumentTypes.get(0), argumentTypes.get(1),
                        argumentTypes.get(2), argumentTypes.get(3)));
            default:
                throw err0("Too many arguments to Fn[]", mark);
        }
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
        int nextUnionCaseTag = 0;
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

            var traitMap = Map.fromIterable(allTraits.map(t -> Pair.of(t.getDefinition().getQualifiedName(), t)));
            item.traitsByQualifiedName = traitMap;
        }

        // ==============================================================================
        // compute method map
        // ==============================================================================
        // TODO: Detect conflicting method declarations instead of silently ignoring
        // them
        var methodMap = Map.<String, CJIRIncompleteMethodDescriptor>of();
        var itemTypeArgs = item.getTypeParameters().map(t -> (CJIRType) new CJIRVariableType(t, true));
        for (var methodDefinition : item.getMethods()) {
            if (item.isClass()) {
                if (item.isNative()) {
                    if (methodDefinition.getBody().isPresent()) {
                        throw err0("Methods in native classes should not have bodies", methodDefinition.getMark());
                    }
                } else {
                    if (methodDefinition.getBody().isEmpty()) {
                        throw err0("Methods in non-native classes should have bodies ", methodDefinition.getMark());
                    }
                }
            }
            methodMap.put(methodDefinition.getName(),
                    new CJIRIncompleteMethodDescriptor(item, itemTypeArgs, methodDefinition));
        }
        for (var trait : item.allResolvedTraits) {
            for (var methodDefinition : trait.getDefinition().getMethods()) {
                if (methodDefinition.getBody().isPresent()) {
                    if (methodMap.containsKey(methodDefinition.getName())) {
                        continue;
                    }
                    methodMap.put(methodDefinition.getName(), new CJIRIncompleteMethodDescriptor(trait.getDefinition(),
                            trait.getArguments(), methodDefinition));
                }
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
                        throw err0(selfType + " implements trait " + trait + " but does not implement method "
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
        annotateExpressionWithType(s.getExpression(), context.getDeclaredReturnType());
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
    public Void visitFor(CJAstForStatement s, Void a) {
        annotateExpression(s.getContainerExpression());
        var containerType = s.getContainerExpression().getResolvedType();
        var optIterableTrait = containerType.getImplementingTraitByDefinition(iterableDefinition);
        if (optIterableTrait.isEmpty()) {
            throw err0(containerType + " does not implement the Iterable trait", s.getContainerExpression().getMark());
        }
        var iterableTrait = optIterableTrait.get();
        var itemType = iterableTrait.getArguments().get(0);
        context.enterBlock();
        context.declareVariable(s.getName(), itemType, s.getMark());
        annotateStatement(s.getBody());
        context.exitBlock();
        return null;
    }

    @Override
    public Void visitSwitchUnion(CJAstSwitchUnionStatement s, Void a) {
        annotateExpression(s.getTarget());
        var targetType = s.getTarget().getResolvedType();
        if (!targetType.isUnion()) {
            throw err0("Expected a union type for switch, but got " + targetType, s.getMark());
        }
        var classType = (CJIRClassType) targetType;
        for (var unionCase : s.getUnionCases()) {
            context.enterBlock();
            var name = unionCase.getName();
            var optionalUnionCaseDescriptor = classType.getUnionCaseDescriptor(name);
            if (optionalUnionCaseDescriptor.isEmpty()) {
                throw err0("Union case " + name + " not found for " + classType, unionCase.getMark());
            }
            var unionCaseDescriptor = optionalUnionCaseDescriptor.get();
            var argumentTypes = unionCaseDescriptor.getSignature().argumentTypes;
            var valueNames = unionCase.getValueNames();
            var arge = argumentTypes.size();
            var argc = valueNames.size();
            if (argc != arge) {
                throw err0("Union case " + classType + "." + name + " expects " + arge + " arguments but got " + argc,
                        unionCase.getMark());
            }
            for (int i = 0; i < argc; i++) {
                context.declareVariable(valueNames.get(i), argumentTypes.get(i), unionCase.getMark());
            }
            annotateStatement(unionCase.getBody());
            unionCase.descriptor = unionCaseDescriptor;
            context.exitBlock();
        }
        if (s.getDefaultBody().isPresent()) {
            annotateStatement(s.getDefaultBody().get());
        }
        return null;
    }

    @Override
    public Void visitVariableDeclaration(CJAstVariableDeclarationStatement s, Void a) {
        if (s.getType().isPresent()) {
            context.resolveTypeExpression(s.getType().get());
            var expressionType = s.getType().get().getAsIsType();
            annotateExpressionWithType(s.getExpression(), expressionType);
        } else {
            annotateExpression(s.getExpression());
        }
        context.declareVariable(s.getName(), s.getExpression().getResolvedType(), s.getMark());
        return null;
    }

    @Override
    public Void visitAssignment(CJAstAssignmentStatement s, Void a) {
        annotateExpression(s.getExpression());
        return null;
    }

    void annotateExpressionWithOptionalType(CJAstExpression expression, Optional<CJIRType> optionalType) {
        if (expression.getResolvedTypeOrNull() == null) {
            CJIRExpressionComplexityAnnotator.annotate(expression);
            expression.accept(this, optionalType);
            expression.getComplexityFlags();
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

    private Optional<CJIRType> getDeterminedType(CJIRType declaredType, Map<String, CJIRType> binding) {
        if (declaredType instanceof CJIRVariableType) {
            var name = ((CJIRVariableType) declaredType).getDefinition().getName();
            return binding.getOptional(name);
        } else {
            var type = (CJIRClassType) declaredType;
            var argtypes = List.<CJIRType>of();
            for (var arg : type.getArguments()) {
                var optNewArg = getDeterminedType(arg, binding);
                if (optNewArg.isEmpty()) {
                    return optNewArg;
                }
                argtypes.add(optNewArg.get());
            }
            return Optional.of(new CJIRClassType(type.getDefinition(), argtypes));
        }
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
                if (args.size() != memberArgTypes.size()) {
                    throw XError.withMessage("nextArgIndex = " + nextArgIndex + ", memberArgTypes.size() = "
                            + memberArgTypes.size() + ", args.size() = " + args.size());
                }
                var arg = args.get(nextArgIndex);
                var memberArgType = memberArgTypes.get(nextArgIndex);
                nextArgIndex++;

                if (arg instanceof CJAstLambdaExpression
                        && memberArgType.isFunctionType(((CJAstLambdaExpression) arg).getParameterNames().size())
                        && ((CJAstLambdaExpression) arg).getBody() instanceof CJAstReturnStatement) {
                    /**
                     * If the argument is a lambda expression, we deal with it a bit specially. In
                     * particular, there are often cases when the argument types of the expression
                     * are already known, but not its return type.
                     */

                    var rawLambdaArgType = (CJIRClassType) memberArgType;
                    var translatedLambdaArgTypes = List.<CJIRType>of();
                    for (int i = 1; i < rawLambdaArgType.getArguments().size(); i++) {
                        var optTranslatedArgType = getDeterminedType(rawLambdaArgType.getArguments().get(i), map);
                        if (optTranslatedArgType.isEmpty()) {
                            break;
                        }
                        translatedLambdaArgTypes.add(optTranslatedArgType.get());
                    }

                    if (translatedLambdaArgTypes.size() + 1 == rawLambdaArgType.getArguments().size()) {
                        /**
                         * Argument types are known. Try to use it to determine the return type.
                         */
                        var lambdaArg = (CJAstLambdaExpression) arg;
                        var returnStmt = (CJAstReturnStatement) lambdaArg.getBody();
                        var lambdaParamNames = lambdaArg.getParameterNames();
                        {
                            context.enterBlock();
                            for (int i = 0; i < translatedLambdaArgTypes.size(); i++) {
                                context.declareVariable(lambdaParamNames.get(i), translatedLambdaArgTypes.get(i), mark);
                            }
                            annotateExpression(returnStmt.getExpression());
                            context.exitBlock();
                        }
                        stack.add(Pair.of(memberArgType, getFunctionType(mark,
                                returnStmt.getExpression().getResolvedType(), translatedLambdaArgTypes)));
                    } else {
                        annotateExpression(arg);
                        stack.add(Pair.of(memberArgType, arg.getResolvedType()));
                    }
                } else {
                    annotateExpression(arg);
                    stack.add(Pair.of(memberArgType, arg.getResolvedType()));
                }
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

                    // The other thing we can do at this point is, if the type variable had
                    // trait bounds, we can use the bounds to make more inferences
                    for (var bound : variableType.getBounds()) {
                        var optGivenImplTrait = given.getImplementingTraitByDefinition(bound.getDefinition());
                        if (optGivenImplTrait.isPresent()) {
                            var givenImplTrait = optGivenImplTrait.get();
                            for (int i = 0; i < bound.getArguments().size(); i++) {
                                var typeFromBound = bound.getArguments().get(i);
                                var typeFromGiven = givenImplTrait.getArguments().get(i);
                                stack.add(Pair.of(typeFromBound, typeFromGiven));
                            }
                        }
                    }
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
        if (typeArguments.size() > 0) {
            // TODO: Factor out the binding code into one place
            var binding = Map.<String, CJIRType>of();
            {
                binding.put("Self", methodDescriptor.selfType);
                var item = methodDescriptor.item;
                var itemTypeArguments = methodDescriptor.itemTypeArguments;
                var methodTypeArguments = typeArguments;
                var method = methodDescriptor.method;
                for (int i = 0; i < itemTypeArguments.size(); i++) {
                    binding.put(item.getTypeParameters().get(i).getName(), itemTypeArguments.get(i));
                }
                for (int i = 0; i < methodTypeArguments.size(); i++) {
                    binding.put(method.getTypeParameters().get(i).getName(), methodTypeArguments.get(i));
                }
            }
            for (int i = 0; i < typeArguments.size(); i++) {
                var param = methodDescriptor.method.getTypeParameters().get(i);
                var typeArg = typeArguments.get(i);
                for (var rawTrait : param.getBounds().map(t -> t.getAsIsTrait())) {
                    var implTrait = rawTrait.substitute(binding);
                    if (!typeArg.implementsTrait(implTrait)) {
                        throw err0(typeArg + " does not implement " + implTrait, mark);
                    }
                }
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
        } else if (e.getType().equals("Char")) {
            type = charType;
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
    public Void visitListDisplay(CJAstListDisplayExpression e, Optional<CJIRType> a) {
        var elements = e.getElements();
        CJIRType elementType;
        if (a.isPresent()) {
            var listType = a.get();
            if (!listType.isDerivedFrom(e.isMutable() ? mutableListDefinition : listDefinition)) {
                throw err0("Expected " + listType + " but got list display", e.getMark());
            }
            elementType = ((CJIRClassType) listType).getArguments().get(0);
            e.resolvedType = listType;
        } else {
            if (elements.size() == 0) {
                throw err0("Could not determine type of list", e.getMark());
            }
            annotateExpression(elements.get(0));
            elementType = elements.get(0).getResolvedType();
            e.resolvedType = e.isMutable() ? getMutableListTypeOf(elementType) : getListTypeOf(elementType);
        }
        for (var element : elements) {
            annotateExpressionWithType(element, elementType);
        }
        return null;
    }

    @Override
    public Void visitTupleDisplay(CJAstTupleDisplayExpression e, Optional<CJIRType> a) {
        var elements = e.getElements();
        List<CJIRType> elementTypes;
        if (a.isPresent()) {
            var tupleType = a.get();
            if (!tupleType.isTupleType(elements.size())) {
                throw err0("Expected " + tupleType + " but got Tuple" + elements.size() + " display", e.getMark());
            }
            elementTypes = ((CJIRClassType) tupleType).getArguments();
        } else {
            for (var element : elements) {
                annotateExpression(element);
            }
            elementTypes = elements.map(el -> el.getResolvedType());
        }
        e.resolvedType = getTupleTypeOf(e.getMark(), elementTypes);
        return null;
    }

    @Override
    public Void visitLambda(CJAstLambdaExpression e, Optional<CJIRType> a) {
        if (a.isEmpty()) {
            throw err0("Lambda expressions require type ascriptions", e.getMark());
        }
        var requiredType = a.get();
        if (!requiredType.isFunctionType(e.getParameterNames().size())) {
            var fnTypeName = "Fn" + e.getParameterNames().size();
            throw err0("Expected " + requiredType + " but got a " + fnTypeName, e.getMark());
        }
        var fnType = (CJIRClassType) requiredType;
        {
            var returnType = fnType.getArguments().get(0);
            context.enterLambda(returnType);
            var argumentTypes = fnType.getArguments().sliceFrom(1);
            var parameterNames = e.getParameterNames();
            Assert.equals(argumentTypes.size(), parameterNames.size());
            for (int i = 0; i < argumentTypes.size(); i++) {
                context.declareVariable(parameterNames.get(i), argumentTypes.get(i), e.getMark());
            }
            annotateStatement(e.getBody());
            context.exitLambda();
        }
        e.resolvedType = requiredType;
        return null;
    }

    @Override
    public Void visitCompound(CJAstCompoundExpression e, Optional<CJIRType> a) {
        context.enterBlock();
        for (var statement : e.getStatements()) {
            annotateStatement(statement);
        }
        if (e.getExpression().isEmpty()) {
            e.resolvedType = unitType;
        } else {
            annotateExpressionWithOptionalType(e.getExpression().get(), a);
            e.resolvedType = e.getExpression().get().getResolvedType();
        }
        context.exitBlock();
        return null;
    }

    @Override
    public Void visitLogicalNot(CJAstLogicalNotExpression e, Optional<CJIRType> a) {
        annotateExpressionWithType(e.getInner(), boolType);
        e.resolvedType = boolType;
        return null;
    }

    @Override
    public Void visitLogicalBinary(CJAstLogicalBinaryExpression e, Optional<CJIRType> a) {
        annotateExpressionWithType(e.getLeft(), boolType);
        annotateExpressionWithType(e.getRight(), boolType);
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
