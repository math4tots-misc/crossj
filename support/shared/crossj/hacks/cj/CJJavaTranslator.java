package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Pair;
import crossj.base.Str;
import crossj.base.XError;

/**
 * Translates CJAst* to Java source.
 *
 * Every CJ class requires two (final) Java classes, one for the meta, and one
 * for data. The meta classes holds all the methods and the data class holds all
 * the fields.
 *
 * CJ traits translate into exactly one Java interface to be implemented by the
 * meta classes.
 */
public final class CJJavaTranslator {

    public static List<CJJavaClass> emitAll(CJIRWorld world) {
        var list = List.<CJJavaClass>of();
        for (var item : world.getAllItems()) {
            list.addAll(emit(world, item));
        }
        return list;
    }

    public static List<CJJavaClass> emit(CJIRWorld world, CJAstItemDefinition item) {
        var translator = new CJJavaTranslator(world);
        translator.emitItem(item);
        return translator.javaClasses;
    }

    private CJJavaTranslator(CJIRWorld world) {
        this.world = world;
    }

    private final CJIRWorld world;
    private String packageName = "";
    private String shortName = "";
    private String translatedPackageName = "";
    private String dataClassName = "";
    private String metaClassName = "";
    private String traitClassName = "";
    private CJStrBuilder sb = null;
    private final List<CJJavaClass> javaClasses = List.of();

    private static String translatePackageName(String packageName) {
        return "cj." + packageName;
    }

    private static String translateVariableName(String variableName) {
        return "cv" + variableName;
    }

    private static String translateFieldName(String fieldName) {
        return "cf" + fieldName;
    }

    private static Pair<String, String> splitQualifiedName(String qualifiedName) {
        var parts = Str.split(qualifiedName, ".");
        return Pair.of(Str.join(".", parts.slice(0, parts.size() - 1)), parts.get(parts.size() - 1));
    }

    private static String shortNameToDataClassName(String shortName) {
        if (shortName.equals("Int")) {
            return "int";
        } else if (shortName.equals("Char")) {
            return "char";
        } else if (shortName.equals("Double")) {
            return "double";
        } else if (shortName.equals("String")) {
            return "String";
        } else {
            return "CD" + shortName;
        }
    }

    private static String shortNameToDataClassNameForGeneric(String shortName) {
        if (shortName.equals("Int")) {
            return "Integer";
        } else if (shortName.equals("Char")) {
            return "Character";
        } else if (shortName.equals("Double")) {
            return "Double";
        } else {
            return shortNameToDataClassName(shortName);
        }
    }

    public static String shortNameToMetaClassName(String shortName) {
        return "CM" + shortName;
    }

    public static String shortnameToTraitClassName(String shortName) {
        return "CT" + shortName;
    }

    private void emitItem(CJAstItemDefinition item) {
        packageName = item.getPackageName();
        shortName = item.getShortName();
        translatedPackageName = translatePackageName(packageName);
        dataClassName = shortNameToDataClassName(shortName);
        metaClassName = shortNameToMetaClassName(shortName);
        traitClassName = shortnameToTraitClassName(shortName);
        if (item.isTrait()) {
        } else {
            emitClass(item);
        }
    }

    private void emitClass(CJAstItemDefinition item) {
        emitClassDataClass(item);
    }

    private void emitImports(CJAstItemDefinition item) {
        for (var qualifiedName : item.getImports()) {
            var pair = splitQualifiedName(qualifiedName);
            if (pair.get1().equals(packageName)) {
                // items in the same package do not need to be imported.
                continue;
            }
            if (world.isClass(qualifiedName)) {
                var pkg = translatePackageName(pair.get1());
                var dataClassName = shortNameToDataClassName(pair.get2());
                var metaClassName = shortNameToMetaClassName(pair.get2());
                sb.line("import " + pkg + "." + dataClassName + ";");
                sb.line("import " + pkg + "." + metaClassName + ";");
            } else if (world.isTrait(qualifiedName)) {
                var pkg = translatePackageName(pair.get1());
                var traitClassName = shortnameToTraitClassName(pair.get2());
                sb.line("import " + pkg + "." + traitClassName + ";");
            } else {
                throw XError.withMessage(qualifiedName + " is not a known trait or class");
            }
        }
    }

    private void emitClassDataClass(CJAstItemDefinition item) {
        sb = new CJStrBuilder();
        sb.line("package " + translatedPackageName + ";");
        emitImports(item);
        sb.line("public final class " + dataClassName + " {");
        sb.indent();
        var fields = item.getMembers().filter(m -> m instanceof CJAstFieldDefinition)
                .map(m -> (CJAstFieldDefinition) m);

        // define the constructor
        sb.lineStart("public " + dataClassName + "(");
        if (fields.size() > 0) {
            var fieldType = translateTypeExpressionAsDataType(fields.get(0).getType());
            var fieldName = translateFieldName(fields.get(0).getName());
            sb.lineBody(fieldType + " " + fieldName);
            for (int i = 1; i < fields.size(); i++) {
                fieldType = translateTypeExpressionAsDataType(fields.get(i).getType());
                fieldName = translateFieldName(fields.get(i).getName());
                sb.lineBody(", " + fieldType + " " + fieldName);
            }
        }
        sb.lineEnd(") {");
        sb.indent();
        for (var field : fields) {
            var fieldName = translateFieldName(field.getName());
            sb.line("this." + fieldName + " = " + fieldName + ";");
        }
        sb.dedent();
        sb.line("}");

        // declare the fields
        for (var field : fields) {
            sb.lineStart("public ");
            sb.lineBody(translateTypeExpressionAsDataType(field.getType()));
            sb.lineEnd(" " + translateFieldName(field.getName()) + ";");
        }

        sb.dedent();
        sb.line("}");
        javaClasses.add(new CJJavaClass(translatedPackageName, dataClassName, sb.build()));
        sb = null;
    }

    // Type expressions can be translated in 3 ways:
    // - as a data object's java-class (this may need to be split depending on
    // Java's generics)
    // - as the meta object's java-class
    // - as the meta object's java-expression, for retrieving the meta object

    private String translateTypeExpressionAsDataType(CJAstTypeExpression type) {
        var args = type.getArguments();
        if (args.size() == 0) {
            return shortNameToDataClassName(type.getName());
        }
        var sb = Str.builder();
        sb.s(shortNameToDataClassName(type.getName())).s("<").s(translateTypeExpressionAsDataType(args.get(0)));
        for (int i = 1; i < args.size(); i++) {
            sb.s(",").s(translateTypeExpressionAsDataTypeForGeneric(args.get(i)));
        }
        sb.s(">");
        return sb.build();
    }

    private String translateTypeExpressionAsDataTypeForGeneric(CJAstTypeExpression type) {
        var args = type.getArguments();
        if (args.size() == 0) {
            return shortNameToDataClassNameForGeneric(type.getName());
        }
        var sb = Str.builder();
        sb.s(shortNameToDataClassNameForGeneric(type.getName())).s("<")
                .s(translateTypeExpressionAsDataTypeForGeneric(args.get(0)));
        for (int i = 1; i < args.size(); i++) {
            sb.s(",").s(translateTypeExpressionAsDataTypeForGeneric(args.get(i)));
        }
        sb.s(">");
        return sb.build();
    }
}
