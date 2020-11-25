package crossj.hacks.cj;

import crossj.base.FS;
import crossj.base.IO;
import crossj.base.List;
import crossj.base.OS;
import crossj.base.Optional;
import crossj.base.Str;
import crossj.base.XError;

/**
 * Translate CJAst* to JavaScript source.
 *
 */
public final class CJJSTranslator implements CJAstStatementVisitor<Void, Void> {

    private static final int DECLARE_NONE = 1;
    private static final int DECLARE_LET = 2;
    private static final int DECLARE_CONST = 3;

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
        var qualifiedTestClassNames = List.<String>of();
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
            } else if (arg.equals("-t") || arg.equals("--test-class")) {
                i++;
                qualifiedTestClassNames.add(args[i]);
            } else if (arg.equals("--")) {
                // skip
            } else {
                throw XError.withMessage("Unrecognized command line flag: " + arg);
            }
        }
        if (qualifiedMainClassName != null && qualifiedTestClassNames.size() > 0) {
            throw XError.withMessage("main class and test classes cannot both be specified");
        }
        if (qualifiedMainClassName == null && qualifiedTestClassNames.size() == 0) {
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
            var content = qualifiedMainClassName != null ? emitMain(world, qualifiedMainClassName)
                    : emitTest(world, qualifiedTestClassNames);
            IO.writeFile(outFile, content);
        }
    }

    public static String emitMain(CJIRWorld world, String qualifiedMainClassName) {
        // call the main method
        var sb = new CJStrBuilder();
        var mainObjectName = qualifiedNameToMetaObjectName(qualifiedMainClassName);
        var mainMethodName = nameToMethodName("main");
        sb.line(mainObjectName + "." + mainMethodName + "();");
        return emitMainCommon(world, sb.build());
    }

    public static String emitTest(CJIRWorld world, List<String> qualifiedTestClassNames) {
        var sb = new CJStrBuilder();
        sb.line("console.log(\"Running tests\");");
        int testCount = 0;
        for (var qualifiedTestClassName : qualifiedTestClassNames) {
            var item = world.getItemOrNull(qualifiedTestClassName);
            if (item == null) {
                throw XError.withMessage("Test " + qualifiedTestClassName + " not found");
            }
            sb.line("console.log(\"  " + qualifiedTestClassName + "\");");
            var methodNames = item.getMethods().filter(m -> m.getName().startsWith("test")).map(m -> m.getName());
            var jsObjectName = qualifiedNameToMetaObjectName(qualifiedTestClassName);
            for (var methodName : methodNames) {
                testCount++;
                sb.line("process.stdout.write(\"    " + methodName + "... \");");
                var jsMethodName = nameToMethodName(methodName);
                sb.line(jsObjectName + "." + jsMethodName + "();");
                sb.line("console.log(\"ok\")");
            }
        }
        sb.line("console.log(\"" + testCount + " tests (in " + qualifiedTestClassNames.size()
                + " classes) passed\");");
        return emitMainCommon(world, sb.build());
    }

    private static String emitMainCommon(CJIRWorld world, String main) {
        // translate all classes
        var translator = new CJJSTranslator(world);
        translator.sb.line("(function(){");
        translator.sb.line("\"use strict\";");

        var prelude = IO.readFile(getPathToPrelude());
        translator.sb.line(prelude);

        for (var item : world.getAllItems()) {
            translator.emitItem(item);
        }

        // insert main snippet
        translator.sb.line(main);
        translator.sb.line("})();");
        return translator.sb.build();
    }

    private static String getPathToPrelude() {
        return FS.join(OS.getenv("HOME"), "git", "crossj", "support", "app", "crossj.hacks.cj", "prelude.js");
    }

    /**
     * Can this expressio nbe translated with a CJJSSimpleExpressionTranslator?
     * @return
     */
    private static boolean isSimple(CJAstExpression expression) {
        return CJJSSimpleExpressionTranslator.isSimple(expression);
    }

    // private final CJIRWorld world;
    private CJStrBuilder sb = new CJStrBuilder();
    private CJJSTypeTranslator typeTranslator;
    private CJJSSimpleExpressionTranslator simpleExpressionTranslator;
    private int methodLevelUniqueId = 0;

    private CJJSTranslator(CJIRWorld world) {
        // this.world = world;
    }

    static String qualifiedNameToMetaClassName(String qualifiedItemName) {
        return "MC$" + qualifiedItemName.replace(".", "$");
    }

    // only applies when a class has no type parameters
    static String qualifiedNameToMetaObjectName(String qualifiedItemName) {
        return "MO$" + qualifiedItemName.replace(".", "$");
    }

    static String qualifiedNameToConstructorName(String qualifiedItemName) {
        return "CT$" + qualifiedItemName.replace(".", "$");
    }

    static String nameToFunctionLevelTypeVariableName(String shortName) {
        return "TV$" + shortName;
    }

    static String nameToFieldName(String name) {
        return "F$" + name;
    }

    static String nameToLocalVariableName(String name) {
        return "L$" + name;
    }

    static String nameToMethodName(String name) {
        return "M$" + name;
    }

    private void emitItem(CJAstItemDefinition item) {
        typeTranslator = new CJJSTypeTranslator(item);
        simpleExpressionTranslator = new CJJSSimpleExpressionTranslator(typeTranslator);
        if (item.isNative()) {
            // nothing to do
        } else if (item.isTrait()) {
            emitTrait(item);
        } else {
            emitClass(item);
        }
        typeTranslator = null;
        simpleExpressionTranslator = null;
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
        if (item.isUnion()) {
            // union classes don't need need to emit a data class,
            // they are always represented by an array.
            return;
        }
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
        return typeTranslator.translateType(type);
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
        var exprPartial = emitExpressionPartial(s.getExpression());
        sb.line(exprPartial + ";");
        return null;
    }

    @Override
    public Void visitReturn(CJAstReturnStatement s, Void a) {
        var retPartial = emitExpressionPartial(s.getExpression());
        sb.line("return " + retPartial + ";");
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

    private String newMethodLevelUniqueId() {
        var name = "L$" + methodLevelUniqueId;
        methodLevelUniqueId++;
        return name;
    }

    @Override
    public Void visitSwitchUnion(CJAstSwitchUnionStatement s, Void a) {
        var tmpvar = emitExpression(s.getTarget(), Optional.empty(), DECLARE_CONST);
        sb.line("switch (" + tmpvar + "[0]) {");
        sb.indent();
        for (var unionCase : s.getUnionCases()) {
            sb.line("case " + unionCase.getDescriptor().tag + ": {");
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
        return null;
    }

    @Override
    public Void visitVariableDeclaration(CJAstVariableDeclarationStatement s, Void a) {
        emitExpression(s.getExpression(), Optional.of(nameToLocalVariableName(s.getName())), DECLARE_LET);
        return null;
    }

    @Override
    public Void visitAssignment(CJAstAssignmentStatement s, Void a) {
        emitExpression(s.getExpression(), Optional.of(nameToLocalVariableName(s.getName())), DECLARE_NONE);
        return null;
    }

    /**
     * Emits the javascript statements needed to compute the expression, and saves the result to a variable.
     * The variable to save to can be specified if desired. Otherwise a new temporary variable is generated.
     */
    private String emitExpression(CJAstExpression expression, Optional<String> optionalJsVariableName, int declareType) {
        var partial = emitExpressionPartial(expression);
        var jsVariableName = optionalJsVariableName.getOrElseDo(() -> newMethodLevelUniqueId());
        sb.line(getDeclarePrefix(declareType) + jsVariableName + " = " + partial + ";");
        return jsVariableName;
    }

    /**
     * Emits the javascript statements needed to compute the expression, and returns the final javascript
     * expression that would finish the computation.
     *
     * Care needs to be taken with using this method since when the method returns, the expression will be
     * "partially" in the progress of computing the expression. So the returned expression should be added
     * as soon as possible with minimal other computation in between.
     */
    private String emitExpressionPartial(CJAstExpression expression) {
        if (isSimple(expression)) {
            return simpleExpressionTranslator.translateExpression(expression);
        } else {
            throw XError.withMessage("TODO: non-simple emitExpressionPartial");
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
}
