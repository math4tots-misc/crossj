package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.FS;
import crossj.base.IO;
import crossj.base.List;
import crossj.base.OS;
import crossj.base.Str;
import crossj.base.XError;

/**
 * Translate CJAst* to JavaScript source.
 *
 */
public final class CJJSTranslator implements CJAstStatementVisitor<Void, Void>, CJAstExpressionVisitor<String, Void> {

    /**
     * Quick and dirty CLI for translating cj sources to a javascript blob.
     *
     * E.g. usage:
     *
     * <pre>
     * ./run-class crossj.hacks.cj.CJJSTranslator -r src/main/cj -m hacks.Sample | node
     * </pre>
     */
    public static void main(String[] args) {
        String qualifiedMainClassName = null;
        var sourceRoots = List.<String>of();
        String outFile = null;
        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            if (arg.equals("-m") || arg.equals("--main")) {
                i++;
                qualifiedMainClassName = args[i];
            } else if (arg.equals("-r") || arg.equals("--source-root")) {
                i++;
                sourceRoots.add(args[i]);
            } else if (arg.equals("-o")) {
                i++;
                outFile = args[i];
            } else if (arg.equals("--")) {
                // skip
            } else {
                throw XError.withMessage("Unrecognized command line flag: " + arg);
            }
        }
        if (qualifiedMainClassName == null) {
            throw XError.withMessage("No main class specified (specify with '-m')");
        }
        if (outFile == null) {
            throw XError.withMessage("No output file specified (specify with '-o')");
        }

        var world = new CJIRWorld();
        for (var sourceRoot : sourceRoots) {
            for (var path : FS.files(sourceRoot)) {
                if (Str.endsWith(path, ".cj")) {
                    var item = CJParser.parseString(path, IO.readFile(path)).get();
                    world.add(item);
                }
            }
        }
        var tryVoid = CJIRAnnotator.annotate(world);
        if (tryVoid.isFail()) {
            throw XError.withMessage(tryVoid.getErrorMessageWithContext());
        } else {
            IO.writeFile(outFile, emitMain(world, qualifiedMainClassName));
        }
    }

    public static String emitMain(CJIRWorld world, String qualifiedMainClassName) {
        var translator = new CJJSTranslator(world);
        translator.sb.line("(function(){");
        translator.sb.line("\"use strict\";");

        var prelude = IO.readFile(getPathToPrelude());
        translator.sb.line(prelude);

        for (var item : world.getAllItems()) {
            translator.emitItem(item);
        }

        // call the main method
        {
            var mainObjectName = qualifiedNameToMetaObjectName(qualifiedMainClassName);
            var mainMethodName = nameToMethodName("main");
            translator.sb.line(mainObjectName + "." + mainMethodName + "();");
        }
        translator.sb.line("})();");
        return translator.sb.build();
    }

    private static String getPathToPrelude() {
        return FS.join(OS.getenv("HOME"), "git", "crossj", "support", "app", "crossj.hacks.cj", "prelude.js");
    }

    // private final CJIRWorld world;
    private final CJStrBuilder sb = new CJStrBuilder();
    private CJAstItemDefinition currentItem = null;

    private CJJSTranslator(CJIRWorld world) {
        // this.world = world;
    }

    private static String qualifiedNameToMetaClassName(String qualifiedItemName) {
        return "MC$" + qualifiedItemName.replace(".", "$");
    }

    // only applies when a class has no type parameters
    private static String qualifiedNameToMetaObjectName(String qualifiedItemName) {
        return "MO$" + qualifiedItemName.replace(".", "$");
    }

    private static String qualifiedNameToConstructorName(String qualifiedItemName) {
        return "CT$" + qualifiedItemName.replace(".", "$");
    }

    private static String nameToFunctionLevelTypeVariableName(String shortName) {
        return "TV$" + shortName;
    }

    private String nameToItemLevelTypeVariableExpression(String shortName) {
        if (currentItem.isTrait()) {
            return "this.TV$" + currentItem.getQualifiedName().replace(".", "$") + "$" + shortName;
        } else {
            return "this.TV$" + shortName;
        }
    }

    private static String nameToFieldName(String name) {
        return "F$" + name;
    }

    private static String nameToLocalVariableName(String name) {
        return "L$" + name;
    }

    private static String nameToMethodName(String name) {
        return "M$" + name;
    }

    private void emitItem(CJAstItemDefinition item) {
        currentItem = item;
        if (item.isNative()) {
            // nothing to do
        } else if (item.isTrait()) {
            emitTrait(item);
        } else {
            emitClass(item);
        }
        currentItem = null;
    }

    private void emitTrait(CJAstItemDefinition item) {
        var qualifiedItemName = item.getQualifiedName();
        var metaClassName = qualifiedNameToMetaClassName(qualifiedItemName);
        sb.line("class " + metaClassName + " {");
        sb.indent();
        for (var member : item.getMembers()) {
            if (member instanceof CJAstMethodDefinition) {
                var method = (CJAstMethodDefinition) member;
                emitMethod(method);
            }
        }
        sb.dedent();
        sb.line("}");
    }

    private void emitClass(CJAstItemDefinition item) {
        emitDataClass(item);
        emitMetaClass(item);
    }

    private void emitDataClass(CJAstItemDefinition item) {
        var qualifiedItemName = item.getQualifiedName();
        var constructorName = qualifiedNameToConstructorName(qualifiedItemName);
        var fields = item.getMembers().filter(m -> m instanceof CJAstFieldDefinition)
                .map(f -> (CJAstFieldDefinition) f);
        sb.lineStart("function " + constructorName + "(");
        if (fields.size() > 0) {
            sb.lineBody(nameToFieldName(fields.get(0).getName()));
            for (int i = 1; i < fields.size(); i++) {
                sb.lineBody(",");
                sb.lineBody(nameToFieldName(fields.get(i).getName()));
            }
        }
        sb.lineEnd(") {");
        sb.indent();
        sb.line("return {");
        sb.indent();
        for (var field : fields) {
            var fieldName = nameToFieldName(field.getName());
            sb.line(fieldName + ": " + fieldName + ",");
        }
        sb.dedent();
        sb.line("};");
        sb.dedent();
        sb.line("}");
    }

    private void emitMetaClass(CJAstItemDefinition item) {
        var qualifiedItemName = item.getQualifiedName();
        var metaClassName = qualifiedNameToMetaClassName(qualifiedItemName);
        var typeParameters = item.getTypeParameters();
        sb.line("class " + metaClassName + " {");
        sb.indent();
        for (var member : item.getMembers()) {
            if (member instanceof CJAstMethodDefinition) {
                var method = (CJAstMethodDefinition) member;
                emitMethod(method);
            }
        }
        sb.dedent();
        sb.line("}");
        if (typeParameters.size() == 0) {
            var metaObjectName = qualifiedNameToMetaObjectName(qualifiedItemName);
            sb.line("const " + metaObjectName + " = new " + metaClassName + "();");
        }
    }

    private void emitMethod(CJAstMethodDefinition method) {
        if (method.getBody().isEmpty()) {
            return;
        }
        var body = method.getBody().get();
        var methodName = nameToMethodName(method.getName());
        var typeParameters = method.getTypeParameters();
        var parameters = method.getParameters();
        sb.lineStart(methodName + "(");
        {
            boolean first = true;
            for (var typeParameter : typeParameters) {
                if (!first) {
                    sb.lineBody(",");
                }
                first = false;
                sb.lineBody(nameToFunctionLevelTypeVariableName(typeParameter.getName()));
            }
            for (var parameter : parameters) {
                if (!first) {
                    sb.lineBody(",");
                }
                first = false;
                sb.lineBody(nameToLocalVariableName(parameter.getName()));
            }
        }
        sb.lineEnd(") {");
        sb.indent();
        for (var statement : body.getStatements()) {
            emitStatement(statement);
        }
        sb.dedent();
        sb.line("}");
    }

    private String translateType(CJIRType type) {
        if (type instanceof CJIRVariableType) {
            var variableType = (CJIRVariableType) type;
            if (variableType.isItemLevel()) {
                return nameToItemLevelTypeVariableExpression(variableType.getDefinition().getName());
            } else {
                Assert.that(variableType.isMethodLevel());
                return nameToFunctionLevelTypeVariableName(variableType.getDefinition().getName());
            }
        } else {
            var classType = (CJIRClassType) type;
            if (classType.getArguments().size() == 0) {
                return qualifiedNameToMetaObjectName(classType.getDefinition().getQualifiedName());
            } else {
                var sb = Str.builder();
                sb.s("new ").s(qualifiedNameToMetaClassName(classType.getDefinition().getQualifiedName())).s("(");
                var args = classType.getArguments();
                sb.s(translateType(args.get(0)));
                for (int i = 1; i < args.size(); i++) {
                    sb.s(",").s(translateType(args.get(i)));
                }
                sb.s(")");
                return sb.build();
            }
        }
    }

    private void emitStatement(CJAstStatement statement) {
        statement.accept(this, null);
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
        sb.lineStart(translateExpression(s.getExpression()));
        sb.lineEnd(";");
        return null;
    }

    @Override
    public Void visitReturn(CJAstReturnStatement s, Void a) {
        sb.lineStart("return ");
        sb.lineBody(translateExpression(s.getExpression()));
        sb.lineEnd(";");
        return null;
    }

    @Override
    public Void visitIf(CJAstIfStatement s, Void a) {
        sb.lineStart("if (");
        sb.lineBody(translateExpression(s.getCondition()));
        sb.lineEnd(")");
        emitStatement(s.getBody());
        if (s.getOther().isPresent()) {
            sb.line("else");
            emitStatement(s.getOther().get());
        }
        return null;
    }

    @Override
    public Void visitWhile(CJAstWhileStatement s, Void a) {
        sb.lineStart("while (");
        sb.lineBody(translateExpression(s.getCondition()));
        sb.lineEnd(")");
        emitStatement(s.getBody());
        return null;
    }

    @Override
    public Void visitVariableDeclaration(CJAstVariableDeclarationStatement s, Void a) {
        sb.lineStart("var ");
        sb.lineBody(nameToLocalVariableName(s.getName()));
        sb.lineBody(" = ");
        sb.lineBody(translateExpression(s.getExpression()));
        sb.lineEnd(";");
        return null;
    }

    @Override
    public Void visitAssignment(CJAstAssignmentStatement s, Void a) {
        sb.lineStart(nameToLocalVariableName(s.getName()));
        sb.lineBody(" = ");
        sb.lineBody(translateExpression(s.getExpression()));
        sb.lineEnd(";");
        return null;
    }

    private String translateExpression(CJAstExpression expression) {
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

    private String translateMethodCall(CJIRType owner, String methodName, List<CJIRType> typeArguments,
            List<CJAstExpression> args) {
        var sb = Str.builder();
        sb.s(translateType(owner)).s(".").s(nameToMethodName(methodName)).s("(");
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

    @Override
    public String visitName(CJAstNameExpression e, Void a) {
        // For now, names always refer to local variables.
        // In the future, there's also a chance that we may have
        // global field variables here.
        return nameToLocalVariableName(e.getName());
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
    public String visitLogicalNot(CJAstLogicalNotExpression e, Void a) {
        return "(!" + translateExpression(e.getInner()) + ")";
    }

    @Override
    public String visitNew(CJAstNewExpression e, Void a) {
        var sb = Str.builder();
        var type = (CJIRClassType) e.getType().getAsIsType();
        var constructorName = qualifiedNameToConstructorName(type.getDefinition().getQualifiedName());
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
}
