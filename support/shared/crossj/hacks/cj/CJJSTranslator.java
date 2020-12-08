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
public final class CJJSTranslator {
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
        sb.line("console.log(\"" + testCount + " tests (in " + qualifiedTestClassNames.size() + " classes) passed\");");
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

        for (var item : world.getAllItems()) {
            translator.emitMethodInheritance(item);
        }

        // insert main snippet
        translator.sb.line(main);
        translator.sb.line("})();");
        return translator.sb.build();
    }

    private static String getPathToPrelude() {
        return FS.join(OS.getenv("HOME"), "git", "crossj", "support", "app", "crossj.hacks.cj", "prelude.js");
    }

    // private final CJIRWorld world;
    private CJStrBuilder sb = new CJStrBuilder();
    private CJJSTypeTranslator typeTranslator;
    private CJJSStatementAndExpressionTranslator statementAndExpressionTranslator;

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

    static String nameToStaticFieldCacheName(String name) {
        return "FC$" + name;
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

    private void emitMethodInheritance(CJAstItemDefinition item) {
        if (item.isClass()) {
            var metaClassName = qualifiedNameToMetaClassName(item.getQualifiedName());
            for (var pair : List.sorted(item.getMethodMap().pairs())) {
                var methodName = pair.get1();
                var incompleteMethodDescriptor = pair.get2();
                var sourceItem = incompleteMethodDescriptor.item;
                if (sourceItem == item) {
                    continue;
                }
                var sourceMetaClassName = qualifiedNameToMetaClassName(sourceItem.getQualifiedName());
                var jsMethodName = nameToMethodName(methodName);
                sb.line(metaClassName + ".prototype." + jsMethodName + " = " + sourceMetaClassName + ".prototype."
                        + jsMethodName + ";");
            }
        }
    }

    private void emitItem(CJAstItemDefinition item) {
        typeTranslator = new CJJSTypeTranslator(item);
        statementAndExpressionTranslator = new CJJSStatementAndExpressionTranslator(sb, typeTranslator);
        if (item.isNative()) {
            // nothing to do
        } else if (item.isTrait()) {
            emitTrait(item);
        } else {
            emitClass(item);
        }

        /**
         * For native classes, we attach a few methods for dealing with type variables
         * passed to traits.
         */
        if (item.isNative() && item.isClass()) {
            emitTraitTypeParamMethods(item, false);
        }

        typeTranslator = null;
        statementAndExpressionTranslator = null;
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
        var instanceFields = item.getMembers().filter(m -> m instanceof CJAstFieldDefinition)
                .map(f -> (CJAstFieldDefinition) f).filter(f -> !f.isStatic());

        if (item.isWrapperClass()) {
            Assert.equals(instanceFields.size(), 1);
            sb.line("function " + constructorName + "(x) { return x; }");
        } else {
            sb.lineStart("function " + constructorName + "(");
            if (instanceFields.size() > 0) {
                sb.lineBody(nameToFieldName(instanceFields.get(0).getName()));
                for (int i = 1; i < instanceFields.size(); i++) {
                    sb.lineBody(",");
                    sb.lineBody(nameToFieldName(instanceFields.get(i).getName()));
                }
            }
            sb.lineEnd(") {");
            sb.indent();
            sb.line("return {");
            sb.indent();
            for (var field : instanceFields) {
                var fieldName = nameToFieldName(field.getName());
                sb.line(fieldName + ": " + fieldName + ",");
            }
            sb.dedent();
            sb.line("};");
            sb.dedent();
            sb.line("}");
        }
    }

    private void emitMetaClass(CJAstItemDefinition item) {
        /**
         * If isNative is true, emitTraitTypeParamMethods may be called twice for this
         * class
         */
        Assert.that(!item.isNative());

        var qualifiedItemName = item.getQualifiedName();
        var metaClassName = qualifiedNameToMetaClassName(qualifiedItemName);
        var typeParameters = item.getTypeParameters();
        var staticFields = item.getFields().filter(f -> f.isStatic());
        sb.line("class " + metaClassName + " {");
        sb.indent();

        if (typeParameters.size() > 0) {
            var translatedNames = typeParameters
                    .map(p -> typeTranslator.nameToItemLevelClassTypeVariableFieldName(p.getName()));
            sb.line("constructor(" + Str.join(",", translatedNames) + ") {");
            sb.indent();
            for (var translatedName : translatedNames) {
                sb.line("this." + translatedName + " = " + translatedName + ";");
            }
            sb.dedent();
            sb.line("}");
        }

        emitTraitTypeParamMethods(item, true);

        for (var staticField : staticFields) {
            var fieldName = nameToFieldName(staticField.getName());
            var cacheName = nameToStaticFieldCacheName(staticField.getName());
            sb.line(fieldName + "() {");
            sb.indent();
            sb.line("if (this." + cacheName + " === undefined) {");
            sb.indent();
            var partial = statementAndExpressionTranslator.emitExpressionPartial(staticField.getExpression());
            sb.line("this." + cacheName + " = " + partial + ";");
            sb.dedent();
            sb.line("}");
            sb.line("return this." + cacheName + ";");
            sb.dedent();
            sb.line("}");
        }

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

    private void emitTraitTypeParamMethods(CJAstItemDefinition item, boolean insideClass) {
        var qualifiedItemName = item.getQualifiedName();
        var metaClassName = qualifiedNameToMetaClassName(qualifiedItemName);
        for (var implTrait : item.getAllResolvedTraits()) {
            var traitDef = implTrait.getDefinition();
            var traitTypeParams = traitDef.getTypeParameters();
            for (int i = 0; i < traitTypeParams.size(); i++) {
                var traitTypeParam = traitTypeParams.get(i);
                var traitTypeMethodName = CJJSTypeTranslator.nameToItemLevelTraitTypeVariableMethodName(traitDef,
                        traitTypeParam.getName());

                if (insideClass) {
                    sb.line(traitTypeMethodName + "() {");
                } else {
                    sb.line(metaClassName + ".prototype." + traitTypeMethodName + " = function() {");
                }
                sb.indent();
                sb.line("return " + typeTranslator.translateType(implTrait.getArguments().get(i)) + ";");
                sb.dedent();
                sb.line("}");
            }
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
        statementAndExpressionTranslator.enterMethod();
        for (var statement : body.getStatements()) {
            emitStatement(statement);
        }
        statementAndExpressionTranslator.exitMethod();
        sb.dedent();
        sb.line("}");
    }

    private void emitStatement(CJAstStatement statement) {
        statementAndExpressionTranslator.emitStatement(statement);
    }
}
