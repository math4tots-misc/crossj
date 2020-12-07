package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Optional;
import crossj.base.Set;
import crossj.base.Str;
import crossj.base.XError;

public final class CJJSStatementAndExpressionTranslator
        implements CJAstStatementVisitor<Void, Void>, CJAstExpressionVisitor<String, Void> {
    private static final int DECLARE_NONE = 1;
    private static final int DECLARE_LET = 2;
    private static final int DECLARE_CONST = 3;

    /**
     * These are the types that, when used in a for loop, do not have to explicitly
     * call the 'iter' method because javascript's looping iterator mechanism will
     * do the "right thing".
     */
    private static final Set<String> FOR_LOOP_EXEMPT_TYPES = Set.of("cj.List", "cj.MutableList", "cj.Iterator");

    private final CJStrBuilder sb;
    private final CJJSSimpleExpressionTranslator simpleExpressionTranslator;
    private final CJJSTypeTranslator typeTranslator;
    private int methodLevelUniqueId = 0;

    CJJSStatementAndExpressionTranslator(CJStrBuilder sb, CJJSTypeTranslator typeTranslator) {
        this.sb = sb;
        this.typeTranslator = typeTranslator;
        simpleExpressionTranslator = new CJJSSimpleExpressionTranslator(typeTranslator);
    }

    public void emitStatement(CJAstStatement statement) {
        statement.accept(this, null);
    }

    private static String nameToLocalVariableName(String name) {
        return CJJSTranslator.nameToLocalVariableName(name);
    }

    private String translateType(CJIRType type) {
        return typeTranslator.translateType(type);
    }

    public void enterMethod() {
        methodLevelUniqueId = 0;
    }

    public void exitMethod() {
        methodLevelUniqueId = -1;
    }

    private String newMethodLevelUniqueId() {
        Assert.that(methodLevelUniqueId >= 0);
        var name = "L$" + methodLevelUniqueId;
        methodLevelUniqueId++;
        return name;
    }

    /**
     * Can this expression be translated with a CJJSSimpleExpressionTranslator?
     */
    private static boolean isSimple(CJAstExpression expression) {
        return CJJSSimpleExpressionTranslator.isSimple(expression);
    }

    @Override
    public Void visitBlock(CJAstBlockStatement s, Void a) {
        sb.line("{");
        sb.indent();
        for (var statement : s.getStatements()) {
            emitStatement(statement);
        }
        sb.dedent();
        sb.line("}");
        return null;
    }

    @Override
    public Void visitExpression(CJAstExpressionStatement s, Void a) {
        var exprPartial = emitExpressionPartial(s.getExpression());
        if (exprPartial.length() > 0) {
            sb.line(exprPartial + ";");
        }
        return null;
    }

    @Override
    public Void visitReturn(CJAstReturnStatement s, Void a) {
        if (s.getExpression().getResolvedType().isUnitType()) {
            sb.line("return;");
        } else {
            var retPartial = emitExpressionPartial(s.getExpression());
            sb.line("return " + retPartial + ";");
        }
        return null;
    }

    @Override
    public Void visitIf(CJAstIfStatement s, Void a) {
        var condPartial = emitExpressionPartial(s.getCondition());
        sb.line("if (" + condPartial + ")");
        emitStatement(s.getBody());
        if (s.getOther().isPresent()) {
            sb.line("else");
            var other = s.getOther().get();
            if (other instanceof CJAstBlockStatement) {
                emitStatement(other);
            } else {
                var otherIf = (CJAstIfStatement) other;
                if (isSimple(otherIf.getCondition())) {
                    emitStatement(otherIf);
                } else {
                    sb.line("{");
                    sb.indent();
                    emitStatement(otherIf);
                    sb.dedent();
                    sb.line("}");
                }
            }
        }
        return null;
    }

    @Override
    public Void visitWhile(CJAstWhileStatement s, Void a) {
        if (isSimple(s.getCondition())) {
            sb.lineStart("while (");
            sb.lineBody(simpleExpressionTranslator.translateExpression(s.getCondition()));
            sb.lineEnd(")");
            emitStatement(s.getBody());
        } else {
            sb.line("while (true) {");
            sb.indent();
            var condPartial = emitExpressionPartial(s.getCondition());
            sb.line("if (!(" + condPartial + ")) { break; }");
            emitStatement(s.getBody());
            sb.dedent();
            sb.line("}");
        }
        return null;
    }

    @Override
    public Void visitFor(CJAstForStatement s, Void a) {
        var target = translateTarget(s.getTarget());
        var containerType = s.getContainerExpression().getResolvedType();
        String container;
        if (containerType.getClassTypeQualifiedName().map(name -> FOR_LOOP_EXEMPT_TYPES.contains(name))
                .getOrElse(false)) {
            container = emitExpressionPartial(s.getContainerExpression());
        } else {
            var jsContainerType = translateType(containerType);
            var jsIterMethodName = CJJSTranslator.nameToMethodName("iter");
            var jsFullMethodRef = jsContainerType + "." + jsIterMethodName;
            var containerPartial = emitExpressionPartial(s.getContainerExpression());
            container = jsFullMethodRef + "(" + containerPartial + ")";
        }
        sb.line("for (const " + target + " of " + container + ") {");
        emitStatement(s.getBody());
        sb.line("}");
        return null;
    }

    @Override
    public Void visitUnionSwitch(CJAstUnionSwitchStatement s, Void a) {
        if (s.getTarget().getResolvedType().isNullableType()) {
            Assert.equals(s.getUnionCases().size(), 2);
            Assert.that(s.getDefaultBody().isEmpty());
            var someCase = s.getUnionCases().filter(c -> c.getDescriptor().getTag() == 0).get(0);
            var noneCase = s.getUnionCases().filter(c -> c.getDescriptor().getTag() == 1).get(0);
            Assert.equals(someCase.getValueNames().size(), 1);
            var valueName = someCase.getValueNames().get(0);
            var tmpvar = emitExpression(s.getTarget(), Optional.empty(), DECLARE_CONST);
            sb.line("if (" + tmpvar + " !== null) {");
            sb.indent();
            sb.line("const " + nameToLocalVariableName(valueName) + " = " + tmpvar + ";");
            emitStatement(someCase.getBody());
            sb.dedent();
            sb.line("} else {");
            sb.indent();
            emitStatement(noneCase.getBody());
            sb.dedent();
            sb.line("}");
        } else {
            var tmpvar = emitExpression(s.getTarget(), Optional.empty(), DECLARE_CONST);
            sb.line("switch (" + tmpvar + "[0]) {");
            sb.indent();
            for (var unionCase : s.getUnionCases()) {
                sb.line("case " + unionCase.getDescriptor().getTag() + ": {");
                sb.indent();
                var valueNames = unionCase.getValueNames();
                sb.line("let [_, " + Str.join(", ", valueNames.map(n -> nameToLocalVariableName(n))) + "] = " + tmpvar
                        + ";");
                emitStatement(unionCase.getBody());
                sb.line("break;");
                sb.dedent();
                sb.line("}");
            }
            if (s.getDefaultBody().isPresent()) {
                sb.line("default:");
                emitStatement(s.getDefaultBody().get());
            }
            sb.dedent();
            sb.line("}");
        }
        return null;
    }

    @Override
    public Void visitRawSwitch(CJAstRawSwitchStatement s, Void a) {
        var tmppartial = emitExpressionPartial(s.getTarget());
        sb.line("switch (" + tmppartial + ") {");
        sb.indent();
        for (var case_ : s.getCases()) {
            for (var value : case_.getValues()) {
                sb.line("case " + simpleExpressionTranslator.translateExpression(value) + ":");
            }
            sb.line("{");
            sb.indent();
            emitStatement(case_.getBody());
            sb.line("break;");
            sb.dedent();
            sb.line("}");
        }
        if (s.getDefaultCase().isPresent()) {
            sb.line("default: {");
            sb.indent();
            emitStatement(s.getDefaultCase().get());
            sb.dedent();
            sb.line("}");
        }
        sb.dedent();
        sb.line("}");
        return null;
    }

    @Override
    public Void visitVariableDeclaration(CJAstVariableDeclarationStatement s, Void a) {
        emitExpression(s.getExpression(), Optional.of(translateTarget(s.getTarget())),
                s.isMutable() ? DECLARE_LET : DECLARE_CONST);
        return null;
    }

    @Override
    public Void visitAssignment(CJAstAssignmentStatement s, Void a) {
        emitExpression(s.getExpression(), Optional.of(emitExtendedTarget(s.getTarget())), DECLARE_NONE);
        return null;
    }

    @Override
    public Void visitAugmentedAssignment(CJAstAugmentedAssignmentStatement s, Void a) {
        String target;
        if (s.getOwner().isPresent()) {
            var tmpvar = emitExpressionConst(s.getOwner().get());
            target = tmpvar + "." + CJJSTranslator.nameToFieldName(s.getName());
        } else if (s.getTypeOwner().isPresent()) {
            target = translateType(s.getTypeOwner().get().getAsIsType()) + "."
                    + CJJSTranslator.nameToStaticFieldCacheName(s.getName());
        } else {
            target = nameToLocalVariableName(s.getName());
        }
        var expr = emitExpressionPartial(s.getExpression());
        sb.line(target + s.getType() + expr + ";");
        return null;
    }

    private String emitExtendedTarget(CJAstExtendedAssignmentTarget target) {
        if (target instanceof CJAstAssignmentTarget) {
            return translateTarget((CJAstAssignmentTarget) target);
        } else if (target instanceof CJAstFieldAccessTarget) {
            var t = (CJAstFieldAccessTarget) target;
            var owner = emitExpressionPartial(t.getOwner());
            return owner + "." + CJJSTranslator.nameToFieldName(t.getName());
        } else {
            var t = (CJAstStaticFieldTarget) target;
            var owner = translateType(t.getOwner().getAsIsType());
            return owner + "." + CJJSTranslator.nameToStaticFieldCacheName(t.getName());
        }
    }

    private String translateTarget(CJAstAssignmentTarget target) {
        if (target instanceof CJAstNameTarget) {
            return nameToLocalVariableName(((CJAstNameTarget) target).getName());
        } else {
            return "[" + Str.join(",", ((CJAstTupleTarget) target).getSubtargets().map(t -> translateTarget(t))) + "]";
        }
    }

    /**
     * Emits the javascript statements needed to compute the expression, and saves
     * the result to a variable. The variable to save to can be specified if
     * desired. Otherwise a new temporary variable is generated.
     */
    String emitExpression(CJAstExpression expression, Optional<String> optionalJsVariableName, int declareType) {
        var partial = emitExpressionPartial(expression);
        var jsVariableName = optionalJsVariableName.getOrElseDo(() -> newMethodLevelUniqueId());
        sb.line(getDeclarePrefix(declareType) + jsVariableName + " = " + partial + ";");
        return jsVariableName;
    }

    /**
     * Emits the javascript statements needed to compute the expression.
     *
     * Returns a constant expression that can be used to refer to the expression.
     * This expression should also have no side effects.
     *
     * This method will fall back to `emitExpression(expression, Optional.empty(),
     * DECLARE_CONST)`, except in cases where it is known that the resulting
     * expression is cheap to compute and has no side effects, where it will try to
     * omit as few new temporary variables as possible.
     */
    String emitExpressionConst(CJAstExpression expression) {
        if (
        /**
         * literal expressions in general can just be returned as is
         */
        expression instanceof CJAstLiteralExpression
                /**
                 * Name expressions are also basically fast and have no side effects
                 */
                || expression instanceof CJAstNameExpression
                /**
                 * lambda expressions will either result in a lambda expression literal, or the
                 * name of the function. Either case, it should be ok to return here.
                 */
                || expression instanceof CJAstLambdaExpression) {
            return emitExpressionPartial(expression);
        } else {
            return emitExpression(expression, Optional.empty(), DECLARE_CONST);
        }
    }

    /**
     * Emits the javascript statements needed to compute the expression, and returns
     * the final javascript expression that would finish the computation.
     *
     * Care needs to be taken with using this method since when the method returns,
     * the expression will be "partially" in the progress of computing the
     * expression. So the returned expression should be added as soon as possible
     * with minimal other computation in between.
     */
    String emitExpressionPartial(CJAstExpression expression) {
        if (isSimple(expression)) {
            return simpleExpressionTranslator.translateExpression(expression);
        } else {
            return expression.accept(this, null);
        }
    }

    private String getDeclarePrefix(int declareType) {
        switch (declareType) {
            case DECLARE_NONE:
                return "";
            case DECLARE_LET:
                return "let ";
            case DECLARE_CONST:
                return "const ";
            default:
                throw XError.withMessage("Invalid declare type " + declareType);
        }
    }

    @Override
    public String visitMethodCall(CJAstMethodCallExpression e, Void a) {
        var owner = e.getOwner().getAsIsType();
        var methodName = e.getName();
        var typeArguments = e.getTypeArguments().map(t -> t.getAsIsType());
        var args = e.getArguments();
        return emitMethodCall(owner, methodName, typeArguments, args);
    }

    @Override
    public String visitStaticMethodCall(CJAstStaticMethodCallExpression e, Void a) {
        var owner = e.getOwner().getAsIsType();
        var methodName = e.getName();
        var typeArguments = e.getInferredTypeArguments();
        var args = e.getArguments();
        return emitMethodCall(owner, methodName, typeArguments, args);
    }

    @Override
    public String visitInstanceMethodCall(CJAstInstanceMethodCallExpression e, Void a) {
        var owner = e.getInferredOwnerType();
        var methodName = e.getName();
        var typeArguments = e.getInferredTypeArguments();
        var args = e.getArguments();
        return emitMethodCall(owner, methodName, typeArguments, args);
    }

    private String emitMethodCall(CJIRType owner, String methodName, List<CJIRType> typeArguments,
            List<CJAstExpression> args) {
        var argtmpvars = args.map(arg -> emitExpressionConst(arg));
        return combineMethodCall(typeTranslator, owner, methodName, typeArguments, argtmpvars,
                args.map(arg -> arg.getResolvedType()));
    }

    static String combineMethodCall(CJJSTypeTranslator typeTranslator, CJIRType owner, String cjMethodName,
            List<CJIRType> typeArguments, List<String> args, List<CJIRType> argTypes) {
        CJIRClassType classType = owner instanceof CJIRClassType ? (CJIRClassType) owner : null;
        if (classType != null) {
            var name = classType.getDefinition().getQualifiedName() + "." + cjMethodName;
            var f = CJJSSpecialMethods.OPS.getOrNull(name);
            if (f != null) {
                var result = f.apply(argTypes, args);
                if (result.isPresent()) {
                    return result.get();
                }
            }
        }

        var sb = Str.builder();
        sb.s(typeTranslator.translateType(owner)).s(".").s(CJJSTranslator.nameToMethodName(cjMethodName)).s("(");
        {
            boolean first = true;
            for (var typeArg : typeArguments) {
                if (!first) {
                    sb.s(",");
                }
                first = false;
                sb.s(typeTranslator.translateType(typeArg));
            }
            for (var arg : args) {
                if (!first) {
                    sb.s(",");
                }
                first = false;
                sb.s(arg);
            }
        }
        sb.s(")");
        return sb.build();
    }

    @Override
    public String visitName(CJAstNameExpression e, Void a) {
        // NOTE: we should never actually get here
        // Assert.that(false);
        return simpleExpressionTranslator.translateExpression(e);
    }

    @Override
    public String visitLiteral(CJAstLiteralExpression e, Void a) {
        // NOTE: we should never actually get here
        // Assert.that(false);
        return simpleExpressionTranslator.translateExpression(e);
    }

    @Override
    public String visitStaticFieldAccess(CJAstStaticFieldAccessExpression e, Void a) {
        // NOTE: we should never actually get here
        // Assert.that(false);
        var owner = translateType(e.getOwner().getAsIsType());
        return owner + "." + CJJSTranslator.nameToFieldName(e.getName()) + "()";
    }

    @Override
    public String visitFieldAccess(CJAstFieldAccessExpression e, Void a) {
        var owner = emitExpressionPartial(e.getOwner());
        return owner + "." + CJJSTranslator.nameToFieldName(e.getName());
    }

    @Override
    public String visitNew(CJAstNewExpression e, Void a) {
        var argtmpvars = e.getArguments().map(arg -> emitExpressionConst(arg));
        var sb = Str.builder();
        var type = (CJIRClassType) e.getType().getAsIsType();
        var constructorName = CJJSTranslator.qualifiedNameToConstructorName(type.getDefinition().getQualifiedName());
        sb.s(constructorName).s("(");
        if (argtmpvars.size() > 0) {
            sb.s(argtmpvars.get(0));
            for (int i = 1; i < argtmpvars.size(); i++) {
                sb.s(",").s(argtmpvars.get(i));
            }
        }
        sb.s(")");
        return sb.build();
    }

    @Override
    public String visitNewUnion(CJAstNewUnionExpression e, Void a) {
        if (e.getResolvedType().isNullableType()) {
            switch (e.getResolvedUnionCaseDescriptor().getTag()) {
                case 0: {
                    // Some(T)
                    Assert.equals(e.getArguments().size(), 1);
                    return emitExpressionPartial(e.getArguments().get(0));
                }
                case 1: {
                    // None
                    Assert.equals(e.getArguments().size(), 0);
                    return "null";
                }
                default:
                    throw XError.withMessage("FUBAR");
            }
        } else {
            var argtmpvars = e.getArguments().map(arg -> emitExpressionConst(arg));
            var sb = Str.builder();
            var unionCaseDescriptor = e.getResolvedUnionCaseDescriptor();
            sb.s("[").i(unionCaseDescriptor.getTag());
            for (var argtmpvar : argtmpvars) {
                sb.s(",").s(argtmpvar);
            }
            sb.s("]");
            return sb.build();
        }
    }

    @Override
    public String visitLogicalNot(CJAstLogicalNotExpression e, Void a) {
        return "(!" + emitExpressionPartial(e.getInner()) + ")";
    }

    @Override
    public String visitLogicalBinary(CJAstLogicalBinaryExpression e, Void a) {
        Assert.that(e.isAnd() || e.isOr());
        var tmpvar = emitExpression(e.getLeft(), Optional.empty(), DECLARE_LET);
        sb.line("if (" + (e.isAnd() ? "" : "!") + tmpvar + ") {");
        sb.indent();
        emitExpression(e.getRight(), Optional.of(tmpvar), DECLARE_NONE);
        sb.dedent();
        sb.line("}");
        return tmpvar;
    }

    @Override
    public String visitConditional(CJAstConditionalExpression e, Void a) {
        var tmpvar = newMethodLevelUniqueId();
        sb.line("let " + tmpvar + ";");
        var conditionPartial = emitExpressionPartial(e.getCondition());
        sb.line("if (" + conditionPartial + ") {");
        sb.indent();
        sb.line(tmpvar + " = " + emitExpressionPartial(e.getLeft()) + ";");
        sb.dedent();
        sb.line("} else {");
        sb.indent();
        sb.line(tmpvar + " = " + emitExpressionPartial(e.getRight()) + ";");
        sb.dedent();
        sb.line("}");
        return tmpvar;
    }

    @Override
    public String visitEmptyMutableList(CJAstEmptyMutableListExpression e, Void a) {
        // NOTE: we should never actually get here
        // Assert.that(false);
        return simpleExpressionTranslator.translateExpression(e);
    }

    @Override
    public String visitLambda(CJAstLambdaExpression e, Void a) {
        var tmpvar = newMethodLevelUniqueId();
        sb.line("const " + tmpvar + "=(" + Str.join(",", e.getParameterNames().map(p -> nameToLocalVariableName(p)))
                + ") => {");
        emitStatement(e.getBody());
        sb.line("}");
        return tmpvar;
    }

    @Override
    public String visitCompound(CJAstCompoundExpression e, Void a) {
        if (e.getStatements().size() == 0) {
            if (e.getExpression().isEmpty()) {
                return "";
            } else {
                return emitExpressionPartial(e.getExpression().get());
            }
        }
        if (e.getExpression().isPresent()) {
            var tmpvar = newMethodLevelUniqueId();
            sb.line("let " + tmpvar + ";");
            sb.line("{");
            sb.indent();
            for (var statement : e.getStatements()) {
                emitStatement(statement);
            }
            emitExpression(e.getExpression().get(), Optional.of(tmpvar), DECLARE_NONE);
            sb.dedent();
            sb.line("}");
            return tmpvar;
        } else {
            sb.line("{");
            sb.indent();
            for (var statement : e.getStatements()) {
                emitStatement(statement);
            }
            sb.dedent();
            sb.line("}");
            return "";
        }
    }

    @Override
    public String visitErrorPropagation(CJAstErrorPropagationExpression e, Void a) {
        var inner = emitExpression(e.getInner(), Optional.empty(), DECLARE_CONST);
        sb.line("if (" + inner + "[0] === 1) { return " + inner + "; }");
        return inner + "[1]";
    }

    @Override
    public String visitListDisplay(CJAstListDisplayExpression e, Void a) {
        var argtmpvars = e.getElements().map(arg -> emitExpressionConst(arg));
        return "[" + Str.join(",", argtmpvars) + "]";
    }

    @Override
    public String visitTupleDisplay(CJAstTupleDisplayExpression e, Void a) {
        var argtmpvars = e.getElements().map(arg -> emitExpressionConst(arg));
        return "[" + Str.join(",", argtmpvars) + "]";
    }

    @Override
    public String visitUnionMatch(CJAstUnionMatchExpression e, Void a) {
        if (e.getTarget().getResolvedType().isNullableType()) {
            var someCase = e.getCases().filter(c -> c.getDescriptor().getTag() == 0).get(0);
            var noneCase = e.getCases().filter(c -> c.getDescriptor().getTag() == 1).get(0);
            Assert.equals(someCase.getValueNames().size(), 1);
            var valueName = someCase.getValueNames().get(0);

            var tmpvar = emitExpression(e.getTarget(), Optional.empty(), DECLARE_CONST);
            var outvar = newMethodLevelUniqueId();
            sb.line("let " + outvar + ";");
            sb.line("if (" + tmpvar + " !== null) {");
            sb.indent();
            sb.line("const " + nameToLocalVariableName(valueName) + " = " + tmpvar + ";");
            emitExpression(someCase.getExpression(), Optional.of(outvar), DECLARE_NONE);
            sb.dedent();
            sb.line("} else {");
            sb.indent();
            emitExpression(noneCase.getExpression(), Optional.of(outvar), DECLARE_NONE);
            sb.dedent();
            sb.line("}");
            return outvar;
        } else {
            var tmpvar = emitExpression(e.getTarget(), Optional.empty(), DECLARE_CONST);
            var outvar = newMethodLevelUniqueId();
            sb.line("let " + outvar + ";");
            sb.line("switch (" + tmpvar + "[0]) {");
            sb.indent();
            for (var unionCase : e.getCases()) {
                sb.line("case " + unionCase.getDescriptor().getTag() + ": {");
                sb.indent();
                var valueNames = unionCase.getValueNames();
                sb.line("const [_, " + Str.join(", ", valueNames.map(n -> nameToLocalVariableName(n))) + "] = " + tmpvar
                        + ";");
                emitExpression(unionCase.getExpression(), Optional.of(outvar), DECLARE_NONE);
                sb.line("break;");
                sb.dedent();
                sb.line("}");
            }
            sb.line("default: {");
            sb.indent();
            if (e.getDefaultCase().isPresent()) {
                emitExpression(e.getDefaultCase().get(), Optional.of(outvar), DECLARE_NONE);
            } else {
                sb.line("throw new Error('MISSING UNION CASE MATCH');");
            }
            sb.dedent();
            sb.line("}");
            sb.dedent();
            sb.line("}");
            return outvar;
        }
    }

    @Override
    public String visitRawMatch(CJAstRawMatchExpression e, Void a) {
        var tmppartial = emitExpressionPartial(e.getTarget());
        var outvar = newMethodLevelUniqueId();
        sb.line("let " + outvar + ";");
        sb.line("switch (" + tmppartial + ") {");
        sb.indent();
        for (var case_ : e.getCases()) {
            for (var value : case_.getValues()) {
                sb.line("case " + simpleExpressionTranslator.translateExpression(value) + ":");
            }
            sb.line("{");
            sb.indent();
            var body = emitExpressionPartial(case_.getBody());
            sb.line(outvar + " = " + body + ";");
            sb.line("break;");
            sb.dedent();
            sb.line("}");
        }
        if (e.getDefaultCase().isPresent()) {
            sb.line("default: {");
            sb.indent();
            var body = emitExpressionPartial(e.getDefaultCase().get());
            sb.line(outvar + " = " + body + ";");
            sb.dedent();
            sb.line("}");
        } else {
            sb.line("default: throw new Error('MISSING RAW CASE MATCH');");
        }
        sb.dedent();
        sb.line("}");
        return outvar;
    }
}
