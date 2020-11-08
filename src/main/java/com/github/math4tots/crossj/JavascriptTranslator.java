package com.github.math4tots.crossj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import crossj.base.IO;
import crossj.base.List;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Set;
import crossj.base.XError;

public final class JavascriptTranslator implements ITranslator {
    private String outputFile;
    private Optional<String> main = Optional.empty();
    private StringBuilder sb = new StringBuilder();
    private String filepath;
    private TypeDeclaration currentTypeDeclaration = null;
    private ITypeBinding currentTypeDeclarationBinding = null;
    private List<Pair<String, String>> tests = List.of();

    public JavascriptTranslator() {
        sb.append("const $CJ=(function(){\n");
        sb.append(IO.readResource("prelude.js"));
    }

    @Override
    public void setOutputDirectory(String path) {
        setOutputPath(path + File.separator + "bundle.js");
    }

    public void setOutputPath(String path) {
        outputFile = path;
    }

    @Override
    public void setMain(String main) {
        this.main = Optional.of(main);
    }

    @Override
    public void translate(String filepath, CompilationUnit compilationUnit) {
        this.filepath = filepath;
        for (Object type : compilationUnit.types()) {
            if (type instanceof TypeDeclaration) {
                translateTypeDeclaration((TypeDeclaration) type);
            } else if (type instanceof AnnotationTypeDeclaration) {
                // todo
            } else {
                // unrecognized declaration
                throw err("Unrecognized toplevel: " + type.getClass(), (ASTNode) type);
            }
        }
    }

    @Override
    public XError err(String message, ASTNode... nodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("While translating " + filepath + "\n");
        for (ASTNode node : nodes) {
            CompilationUnit compilationUnit = (CompilationUnit) node.getRoot();
            int line = compilationUnit.getLineNumber(node.getStartPosition());
            sb.append("  in " + getCompilationUnitName(node) + " on line " + line + "\n");
        }
        sb.append(message);
        return XError.withMessage(sb.toString());
    }

    private String getCompilationUnitName(ASTNode node) {
        CompilationUnit compilationUnit = (CompilationUnit) node.getRoot();
        String packageName = compilationUnit.getPackage().getName().getFullyQualifiedName();
        for (Object type : compilationUnit.types()) {
            if (type instanceof TypeDeclaration) {
                return packageName + "." + ((TypeDeclaration) type).getName().getFullyQualifiedName();
            } else if (type instanceof AnnotationTypeDeclaration) {
                return packageName + "." + ((AnnotationTypeDeclaration) type).getName().getFullyQualifiedName();
            } else {
                return packageName + ".<unknown>";
            }
        }
        return packageName + ".<unknown>";
    }

    public String emitString() {
        sb.append("const $TESTS=[\n");
        for (Pair<String, String> test : tests) {
            sb.append("['" + test.get1() + "','" + test.get2() + "'],\n");
        }
        sb.append("];\n");
        if (main.isPresent()) {
            String m = main.get();
            sb.append(getClassReference(m) + ".M$main(cliargs);\n");
        }
        sb.append("return $CJ;\n");
        sb.append("})();\n");
        return sb.toString();
    }

    @Override
    public void commit() {
        var outputDirectory = Paths.get(outputFile).getParent();
        if (!Files.isDirectory(outputDirectory)) {
            try {
                Files.createDirectories(outputDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        IO.writeFile(outputFile, emitString());
    }

    private void setCurrentTypeDeclaration(TypeDeclaration currentTypeDeclaration) {
        this.currentTypeDeclaration = currentTypeDeclaration;
        this.currentTypeDeclarationBinding = currentTypeDeclaration.resolveBinding();
    }

    public void translateTypeDeclaration(TypeDeclaration declaration) {
        setCurrentTypeDeclaration(declaration);
        if (!declaration.isInterface() && !ITranslator.isFinal(declaration)
                && !ITranslator.getQualifiedName(declaration).equals("java.lang.Object")
                && !ITranslator.getQualifiedName(declaration).equals("java.lang.Throwable")
                && !ITranslator.getQualifiedName(declaration).equals("java.lang.RuntimeException")) {
            throw err("All crossj classes must be final", declaration);
        }
        if (isNative(declaration)) {
            // don't translate native classes.
            return;
        }

        ITypeBinding typeBinding = declaration.resolveBinding();
        Set<String> foundMethodNames = Set.of();

        // find all the super interfaces of this type
        List<ITypeBinding> superInterfaces = List.of();
        Set<String> superInterfaceNames = Set.of();
        {
            List<ITypeBinding> stack = List.of(typeBinding);
            while (stack.size() > 0) {
                ITypeBinding nextInterface = stack.pop();
                for (ITypeBinding parentInterface : nextInterface.getInterfaces()) {
                    String qualifiedInterfaceName = parentInterface.getErasure().getQualifiedName();
                    if (!superInterfaceNames.contains(qualifiedInterfaceName)) {
                        superInterfaceNames.add(qualifiedInterfaceName);
                        superInterfaces.add(parentInterface);
                        stack.add(parentInterface);
                    }
                }
            }
        }

        String qualifiedName = typeBinding.getQualifiedName();
        String classRef = getClassReference(qualifiedName);
        sb.append("class " + classRef + "{\n");

        // define static field getter methods
        for (FieldDeclaration field : declaration.getFields()) {
            translateField(qualifiedName, field);
        }

        // initialize a default constructor, if one is needed
        boolean hasConstructor = false;
        for (MethodDeclaration method : declaration.getMethods()) {
            hasConstructor = hasConstructor || method.isConstructor();
        }
        if (!hasConstructor) {
            // If a constructor is not explicitly provided, create a default one
            sb.append("constructor(){\n");
            initInstanceFields(declaration);
            sb.append("}\n");
        }

        // define methods
        for (MethodDeclaration method : declaration.getMethods()) {
            foundMethodNames.add(method.getName().toString());
            translateMethod(method);
        }

        // if this class implements 'XIterable', it needs to implement the
        // [Symbol.iterator] method
        if (superInterfaceNames.contains("crossj.base.XIterable")) {
            sb.append("[Symbol.iterator](){return this.M$iter();}");
        }

        sb.append("}\n");
        sb.append("$CJ['" + qualifiedName + "']=" + classRef + ";\n");

        // add all the inherited default methods for this class
        for (ITypeBinding superInterface : superInterfaces) {
            for (IMethodBinding method : superInterface.getDeclaredMethods()) {
                // check all default non-static methods for inheritance
                int modifiers = method.getModifiers();
                if (Modifier.isDefault(modifiers) && !Modifier.isStatic(modifiers)) {
                    String methodName = method.getName();
                    if (!foundMethodNames.contains(methodName)) {
                        foundMethodNames.add(methodName);
                        sb.append(classRef + ".prototype.M$" + methodName + "="
                                + getClassReference(superInterface.getErasure().getQualifiedName()) + ".prototype.M$"
                                + methodName + ";\n");
                    }
                }
            }
        }

        // define toString
        if (foundMethodNames.contains("toString")) {
            sb.append(classRef + ".prototype.toString=function(){return this.M$toString();};\n");
        }

        // add a marker for all the types that this type is a subtype of.
        for (String qualifiedInterfaceName : superInterfaceNames) {
            String tag = "I$" + qualifiedInterfaceName.replace(".", "$");
            sb.append(classRef + ".prototype." + tag + "=true;\n");
        }
    }

    private void initInstanceFields(TypeDeclaration declaration) {
        for (FieldDeclaration field : declaration.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            for (Object obj : field.fragments()) {
                VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
                sb.append("this.F$" + fragment.getName() + "=");
                if (fragment.getInitializer() != null) {
                    translateExpression(fragment.getInitializer());
                } else {
                    ITypeBinding type = field.getType().resolveBinding().getErasure();
                    sb.append(getDefaultValueForType(type, declaration));
                }
                sb.append(";\n");
            }
        }
    }

    private String getDefaultValueForType(ITypeBinding type, ASTNode... nodes) {
        if (type.isPrimitive()) {
            switch (type.getQualifiedName()) {
                case "int":
                case "long":
                case "float":
                case "double":
                    return "0";
                case "char":
                    return "'\\0'";
                case "boolean":
                    return "false";
                default:
                    throw err("Unrecognized primitive type: " + type.getQualifiedName(), nodes);
            }
        } else {
            return "null";
        }
    }

    public void translateField(String qualifiedClassName, FieldDeclaration declaration) {
        for (Object obj : declaration.fragments()) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
            if (Modifier.isStatic(declaration.getModifiers())) {
                // static field
                Expression init = fragment.getInitializer();
                String name = fragment.getName().toString();
                String cacheRef = getClassReference(qualifiedClassName) + ".FC$" + name;

                // getter
                sb.append("static get F$" + name + "(){\n");
                sb.append("if (" + cacheRef + "===undefined){\n");
                sb.append(cacheRef + "=");
                if (init != null) {
                    translateExpression(init);
                } else {
                    sb.append(getDefaultValueForType(declaration.getType().resolveBinding(), fragment));
                }
                sb.append(";\n");
                sb.append("}\n");
                sb.append("return " + cacheRef + ";\n");
                sb.append("}\n");

                if (!Modifier.isFinal(declaration.getModifiers())) {
                    // setter
                    sb.append("static set F$" + name + "(x){\n");
                    sb.append(cacheRef + "=x;\n");
                    sb.append("}\n");
                }

            } else {
                // instance field
                // do nothing here -- this is handled in the constructor
            }
        }
    }

    public String getClassReference(String qualifiedClassName) {
        return "C$" + qualifiedClassName.replace(".", "$");
    }

    public String getClassReference(TypeDeclaration declaration) {
        return getClassReference(declaration.resolveBinding().getQualifiedName());
    }

    public void translateMethod(MethodDeclaration declaration) {
        Block body = declaration.getBody();
        if (body == null) {
            return;
        }
        if (declaration.isConstructor()) {
            sb.append("constructor");
        } else {
            String name = declaration.getName().toString();
            if (ITranslator.isStatic(declaration)) {
                sb.append("static ");

                // check if this static method is a test
                ITranslator.getExtendedModifiers(declaration).forEach(modifier -> {
                    if (modifier instanceof Annotation) {
                        IAnnotationBinding binding = ((Annotation) modifier).resolveAnnotationBinding();
                        if (binding != null) {
                            if (binding.getAnnotationType().getQualifiedName().equals("crossj.base.Test")) {
                                tests.add(Pair.of(currentTypeDeclarationBinding.getQualifiedName(), name));
                            }
                        }
                    }
                });
            }
            sb.append("M$" + name);
        }
        sb.append("(");
        List<SingleVariableDeclaration> parameters = ITranslator.getParameters(declaration);
        boolean isVarargs = declaration.isVarargs();
        int len = isVarargs ? parameters.size() - 1 : parameters.size();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(parameters.get(i).getName().toString());
        }
        if (isVarargs) {
            if (len > 0) {
                sb.append(',');
            }
            sb.append("...");
            sb.append(parameters.get(len).getName().toString());
        }
        sb.append("){\n");
        if (declaration.isConstructor()) {
            // constructors should initialize its fields first
            initInstanceFields(currentTypeDeclaration);
        }
        translateStatement(body);
        sb.append("}\n");
    }

    public void translateStatement(Statement statement) {
        statement.accept(new DefaultVisitor() {
            @Override
            public boolean visit(Block node) {
                sb.append("{\n");
                for (Object obj : node.statements()) {
                    Statement stmt = (Statement) obj;
                    stmt.accept(this);
                }
                sb.append("}\n");
                return false;
            }

            @Override
            public boolean visit(ReturnStatement node) {
                sb.append("return");
                if (node.getExpression() != null) {
                    sb.append(" ");
                    translateExpression(node.getExpression());
                }
                sb.append(";\n");
                return false;
            }

            @Override
            public boolean visit(ExpressionStatement node) {
                translateExpression(node.getExpression());
                sb.append(";\n");
                return false;
            }

            @Override
            public boolean visit(IfStatement node) {
                sb.append("if(");
                translateExpression(node.getExpression());
                sb.append("){\n");
                translateStatement(node.getThenStatement());
                sb.append("}\n");
                Statement elseStmt = node.getElseStatement();
                if (elseStmt != null) {
                    sb.append("else{\n");
                    translateStatement(elseStmt);
                    sb.append("}\n");
                }
                return false;
            }

            @Override
            public boolean visit(SwitchStatement node) {
                sb.append("switch(");
                translateExpression(node.getExpression());
                sb.append("){\n");
                for (Object obj : node.statements()) {
                    translateStatement((Statement) obj);
                }
                sb.append("}\n");
                return false;
            }

            @Override
            public boolean visit(SwitchCase node) {
                for (Object expr : node.expressions()) {
                    sb.append("case ");
                    translateExpression((Expression) expr);
                    sb.append(":\n");
                }
                if (node.isDefault()) {
                    sb.append("default:\n");
                }
                return false;
            }

            @Override
            public boolean visit(BreakStatement node) {
                sb.append("break;\n");
                return false;
            }

            @Override
            public boolean visit(ContinueStatement node) {
                sb.append("continue;\n");
                return false;
            }

            @Override
            public boolean visit(WhileStatement node) {
                sb.append("while(");
                translateExpression(node.getExpression());
                sb.append("){\n");
                translateStatement(node.getBody());
                sb.append("}\n");
                return false;
            }

            @Override
            public boolean visit(ForStatement node) {
                sb.append("for(");
                if (node.initializers().isEmpty()) {
                    sb.append(";");
                } else if (node.initializers().size() == 1) {
                    if (node.initializers().get(0) instanceof VariableDeclarationExpression) {
                        VariableDeclarationExpression expr = (VariableDeclarationExpression) node.initializers().get(0);
                        translateExpression(expr);
                        sb.append(";");
                    } else {
                        translateStatement((Statement) node.initializers().get(0));
                    }
                }

                Expression cond = node.getExpression();
                if (cond != null) {
                    translateExpression(cond);
                }
                sb.append(";");

                for (int i = 0; i < node.updaters().size(); i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    translateExpression((Expression) node.updaters().get(i));
                }
                sb.append("){\n");
                translateStatement(node.getBody());
                sb.append("}\n");
                return false;
            }

            @Override
            public boolean visit(EnhancedForStatement node) {
                sb.append("for(let " + node.getParameter().getName() + " of ");
                translateExpression(node.getExpression());
                sb.append("){\n");
                translateStatement(node.getBody());
                sb.append("}\n");
                return false;
            }

            @Override
            public boolean visit(VariableDeclarationStatement node) {
                for (Object obj : node.fragments()) {
                    VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
                    sb.append("let " + fragment.getName().toString());
                    Expression init = fragment.getInitializer();
                    if (init != null) {
                        sb.append("=");
                        translateExpression(init);
                    }
                    sb.append(";\n");
                }
                return false;
            }

            @Override
            public boolean visit(ThrowStatement node) {
                sb.append("throw ");
                translateExpression(node.getExpression());
                sb.append(";\n");
                return false;
            }

            @Override
            public boolean visit(TryStatement node) {
                sb.append("try{\n");
                translateStatement(node.getBody());
                sb.append("}\n");
                for (Object obj : node.catchClauses()) {
                    CatchClause clause = (CatchClause) obj;
                    sb.append("catch(" + clause.getException().getName() + "){\n");
                    translateStatement(clause.getBody());
                    sb.append("}\n");
                }
                Block fin = node.getFinally();
                if (fin != null) {
                    sb.append("finally{\n");
                    translateStatement(fin);
                    sb.append("}\n");
                }
                return false;
            }
        });
    }

    public void translateExpressionOrThis(Expression expression) {
        if (expression == null) {
            sb.append("this");
        } else {
            translateExpression(expression);
        }
    }

    public void translateExpression(Expression expression) {
        expression.accept(new DefaultVisitor() {

            private void emitArgs(Iterable<?> iterable, int argc, boolean variadic) {
                List<Expression> args = List.fromIterable(iterable).map(a -> (Expression) a);

                int len = args.size();
                if (variadic && args.size() == argc && args.last().resolveTypeBinding().isArray()) {
                    len--;
                }

                boolean first = true;
                for (int i = 0; i < len; i++) {
                    if (!first) {
                        sb.append(',');
                    }
                    first = false;
                    args.get(i).accept(this);
                }

                if (variadic && args.size() == argc && args.last().resolveTypeBinding().isArray()) {
                    if (!first) {
                        sb.append(",");
                    }
                    sb.append("...");
                    args.get(argc - 1).accept(this);
                }
            }

            @Override
            public boolean visit(MethodInvocation node) {
                IMethodBinding method = node.resolveMethodBinding();
                if (Modifier.isStatic(method.getModifiers())) {
                    // static method call
                    ITypeBinding cls = method.getDeclaringClass().getErasure();
                    String qualifiedClassName = cls.getQualifiedName();
                    switch (qualifiedClassName + "." + method.getName()) {
                        case "crossj.base.XIterator.fromParts": {
                            sb.append("$ITERfromParts(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(",");
                            translateExpression((Expression) node.arguments().get(1));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.StrImpl.codeAt": {
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(".charCodeAt(");
                            translateExpression((Expression) node.arguments().get(1));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.StrImpl.charCode": {
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(".charCodeAt(0)");
                            break;
                        }
                        case "crossj.base.StrImpl.toUTF8": {
                            sb.append("$stringToUTF8(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.StrImpl.fromUTF8": {
                            sb.append("$stringFromUTF8(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.StrImpl.toCodePoints": {
                            sb.append("$stringToCodePoints(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.StrImpl.fromCodePoints": {
                            sb.append("$codePointsToString(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.StrImpl.fromSliceOfCodePoints": {
                            sb.append("$sliceOfcodePointsToString(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(",");
                            translateExpression((Expression) node.arguments().get(1));
                            sb.append(",");
                            translateExpression((Expression) node.arguments().get(2));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.Eq.of": {
                            // Equality check is a common operation, so it seems like a good idea to
                            // not require a class lookup for this
                            sb.append("$EQ(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(',');
                            translateExpression((Expression) node.arguments().get(1));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.BigInt.fromInt":
                        case "crossj.base.BigInt.fromDouble":
                        case "crossj.base.BigInt.fromString": {
                            sb.append("BigInt(");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.BigInt.fromHexString": {
                            sb.append("BigInt('0x' + ");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.BigInt.fromOctString": {
                            sb.append("BigInt('0o' + ");
                            translateExpression((Expression) node.arguments().get(0));
                            sb.append(")");
                            break;
                        }
                        case "crossj.base.BigInt.one": {
                            sb.append("1n");
                            break;
                        }
                        case "crossj.base.BigInt.zero": {
                            sb.append("0n");
                            break;
                        }
                        case "java.lang.Boolean.valueOf":
                        case "java.lang.Double.valueOf":
                        case "java.lang.Integer.valueOf": {
                            // these are basically no-ops (in JS, we don't do this boxing explicitly)
                            translateExpression((Expression) node.arguments().get(0));
                            break;
                        }
                        default: {
                            sb.append(getClassReference(qualifiedClassName) + ".M$" + method.getName() + "(");
                            emitArgs(node.arguments(), method.getParameterTypes().length, method.isVarargs());
                            sb.append(")");
                        }
                    }
                } else {
                    // instance method call
                    String qualifiedClassName = method.getDeclaringClass().getErasure().getQualifiedName();
                    String qualifiedName = qualifiedClassName + "." + method.getName();
                    Expression owner = node.getExpression();

                    if (qualifiedClassName.equals("crossj.base.XIterator")) {
                        String funcName = "$ITER" + method.getName();
                        sb.append(funcName + "(");
                        if (owner == null) {
                            sb.append("this");
                        } else {
                            translateExpression(owner);
                        }
                        for (int i = 0; i < node.arguments().size(); i++) {
                            sb.append(',');
                            Expression argument = (Expression) node.arguments().get(i);
                            translateExpression(argument);
                        }
                        sb.append(")");
                    } else {
                        switch (qualifiedName) {
                            case "java.lang.Object.equals": {
                                sb.append("$EQ(");
                                translateExpressionOrThis(owner);
                                sb.append(',');
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "java.lang.Object.hashCode": {
                                sb.append("$HASH(");
                                translateExpression(owner);
                                sb.append(")");
                                break;
                            }
                            case "java.lang.String.length": {
                                translateExpression(owner);
                                sb.append(".length");
                                break;
                            }
                            case "java.lang.String.hashCode": {
                                sb.append("$STRHASH(");
                                translateExpression(owner);
                                sb.append(")");
                                break;
                            }
                            case "java.lang.String.charAt": {
                                translateExpression(owner);
                                sb.append(".charAt(");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "java.lang.String.substring": {
                                translateExpression(owner);
                                sb.append(".substring(");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(",");
                                translateExpression((Expression) node.arguments().get(1));
                                sb.append(")");
                                break;
                            }
                            case "java.lang.String.endsWith": {
                                translateExpression(owner);
                                sb.append(".endsWith(");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "java.lang.String.startsWith": {
                                translateExpression(owner);
                                sb.append(".startsWith(");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "java.lang.Boolean.compareTo":
                            case "java.lang.Double.compareTo":
                            case "java.lang.Integer.compareTo": {
                                sb.append("$NCMP(");
                                translateExpression(owner);
                                sb.append(",");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "java.lang.Comparable.compareTo": {
                                sb.append("$CMP(");
                                translateExpression(owner);
                                sb.append(",");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "java.lang.String.compareTo": {
                                sb.append("$STRCMP(");
                                translateExpression(owner);
                                sb.append(",");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.Func0.apply":
                            case "crossj.base.Func1.apply":
                            case "crossj.base.Func2.apply":
                            case "crossj.base.Func3.apply":
                            case "crossj.base.Func4.apply": {
                                translateExpression(owner);
                                sb.append("(");
                                for (int i = 0; i < node.arguments().size(); i++) {
                                    if (i > 0) {
                                        sb.append(',');
                                    }
                                    translateExpression((Expression) node.arguments().get(i));
                                }
                                sb.append(")");
                                break;
                            }
                            case "java.lang.String.toString":
                            case "java.lang.Integer.toString":
                            case "java.lang.Double.toString":
                            case "java.lang.Object.toString":
                            case "crossj.base.BigInt.toString": {
                                sb.append("(''+");
                                translateExpression(owner);
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.BigInt.add": {
                                sb.append("(");
                                translateExpression(owner);
                                sb.append('+');
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.BigInt.subtract": {
                                sb.append("(");
                                translateExpression(owner);
                                sb.append('-');
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.BigInt.multiply": {
                                sb.append("(");
                                translateExpression(owner);
                                sb.append('*');
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.BigInt.divide": {
                                sb.append("(");
                                translateExpression(owner);
                                sb.append('/');
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.BigInt.remainder": {
                                sb.append("(");
                                translateExpression(owner);
                                sb.append('%');
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.BigInt.pow": {
                                sb.append("(");
                                translateExpression(owner);
                                sb.append("**");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            case "crossj.base.BigInt.gcd": {
                                sb.append("$bigintGCD(");
                                translateExpression(owner);
                                sb.append(",");
                                translateExpression((Expression) node.arguments().get(0));
                                sb.append(")");
                                break;
                            }
                            default: {
                                // map to a corresponding javascript method call
                                if (owner == null) {
                                    sb.append("this");
                                } else {
                                    translateExpression(owner);
                                }
                                sb.append(".M$" + node.getName().toString() + "(");
                                emitArgs(node.arguments(), method.getParameterTypes().length, method.isVarargs());
                                sb.append(")");
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean visit(ClassInstanceCreation node) {
                IMethodBinding method = node.resolveConstructorBinding();
                String qualifiedClassName = method.getDeclaringClass().getErasure().getQualifiedName();
                sb.append("(new (" + getClassReference(qualifiedClassName) + ")(");
                boolean first = true;
                for (Object argobj : node.arguments()) {
                    if (!first) {
                        sb.append(',');
                    }
                    first = false;
                    Expression arg = (Expression) argobj;
                    arg.accept(this);
                }
                sb.append("))");
                return false;
            }

            @Override
            public boolean visit(LambdaExpression node) {
                String expectedTypeName = "crossj.base.Func" + node.parameters().size();
                ITypeBinding type = node.resolveTypeBinding().getErasure();
                if (!type.getQualifiedName().equals(expectedTypeName)) {
                    throw err("Expected lambda expression to have type " + expectedTypeName + " but got type "
                            + type.getQualifiedName(), node);
                }
                sb.append("(");
                for (int i = 0; i < node.parameters().size(); i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    VariableDeclaration parameter = (VariableDeclaration) node.parameters().get(i);
                    sb.append(parameter.getName());
                }
                sb.append(")=>");
                if (node.getBody() instanceof Expression) {
                    sb.append("{return ");
                    translateExpression((Expression) node.getBody());
                    sb.append(";}");
                } else {
                    Block body = (Block) node.getBody();
                    translateStatement(body);
                }
                return false;
            }

            @Override
            public boolean visit(ParenthesizedExpression node) {
                sb.append("(");
                node.getExpression().accept(this);
                sb.append(")");
                return false;
            }

            @Override
            public boolean visit(NullLiteral node) {
                sb.append("null");
                return false;
            }

            @Override
            public boolean visit(BooleanLiteral node) {
                sb.append(node.booleanValue() ? "true" : "false");
                return false;
            }

            @Override
            public boolean visit(NumberLiteral node) {
                sb.append(node.resolveConstantExpressionValue().toString());
                return false;
            }

            @Override
            public boolean visit(CharacterLiteral node) {
                sb.append(node.getEscapedValue());
                return false;
            }

            @Override
            public boolean visit(StringLiteral node) {
                sb.append(node.getEscapedValue());
                return false;
            }

            @Override
            public boolean visit(ArrayCreation node) {
                var type = node.resolveTypeBinding();
                if (type.getDimensions() != 1) {
                    throw err("Multi-dimensional arrays are not supported", node);
                }
                switch (type.getQualifiedName()) {
                    case "int[]": {
                        sb.append("new Int32Array(");

                        var initializer = node.getInitializer();

                        if (initializer == null) {
                            // the array length must be explicitly set
                            var dimensionExpression = (Expression) node.dimensions().get(0);
                            var length = (int) dimensionExpression.resolveConstantExpressionValue();
                            sb.append(length);
                        } else {
                            // values are explicitly provided
                            if (!node.dimensions().isEmpty()) {
                                throw err(
                                        "If an array is initialized with values, " +
                                                "it's length cannot be explicitly specified",
                                        node);
                            }
                            sb.append("[");
                            var expressions = initializer.expressions();
                            for (int i = 0; i < expressions.size(); i++) {
                                if (i > 0) {
                                    sb.append(",");
                                }
                                var expression = (Expression) expressions.get(i);
                                expression.accept(this);
                            }
                            sb.append("]");
                        }

                        sb.append(")");
                        break;
                    }
                    default: {
                        throw err(type.getQualifiedName() + " types are not supported", node);
                    }
                }
                return false;
            }

            @Override
            public boolean visit(ArrayAccess node) {
                sb.append("((");
                node.getArray().accept(this);
                sb.append(")[");
                node.getIndex().accept(this);
                sb.append("])");
                return false;
            }

            @Override
            public boolean visit(SimpleName node) {
                IBinding binding = node.resolveBinding();
                if (binding instanceof IVariableBinding) {
                    IVariableBinding varb = (IVariableBinding) binding;
                    if (varb.isField()) {
                        if (Modifier.isStatic(varb.getModifiers())) {
                            // static field
                            ITypeBinding cls = varb.getDeclaringClass();
                            sb.append(getClassReference(cls.getQualifiedName()));
                            sb.append(".F$" + varb.getName());
                        } else {
                            // instance field
                            // since it's unqualified, 'this' is implied
                            sb.append("this.F$" + varb.getName());
                        }
                    } else {
                        // local variable
                        sb.append(varb.getName());
                    }
                } else {
                    throw err("Unrecognized name binding: " + binding.getClass(), node);
                }
                return false;
            }

            @Override
            public boolean visit(Assignment node) {
                node.getLeftHandSide().accept(this);
                sb.append(node.getOperator());
                node.getRightHandSide().accept(this);
                return false;
            }

            @Override
            public boolean visit(FieldAccess node) {
                IVariableBinding varb = node.resolveFieldBinding();
                if (Modifier.isStatic(varb.getModifiers())) {
                    // static field
                    ITypeBinding cls = varb.getDeclaringClass();
                    sb.append(getClassReference(cls.getQualifiedName()));
                    sb.append(".F$" + varb.getName());
                } else {
                    // instance field
                    var owningType = node.getExpression().resolveTypeBinding();
                    node.getExpression().accept(this);
                    sb.append(getFieldAccessSuffix(owningType, varb.getName()));
                }
                return false;
            }

            private String getFieldAccessSuffix(ITypeBinding owningType, String fieldName) {
                if (owningType.isArray() && fieldName.equals("length")) {
                    return ".length";
                } else {
                    return ".F$" + fieldName;
                }
            }

            @Override
            public boolean visit(QualifiedName node) {
                IBinding binding = node.resolveBinding();
                if (binding instanceof IVariableBinding) {
                    IVariableBinding varb = (IVariableBinding) binding;
                    if (Modifier.isStatic(varb.getModifiers())) {
                        // static field
                        ITypeBinding cls = varb.getDeclaringClass();
                        sb.append(getClassReference(cls.getQualifiedName()));
                        sb.append(".F$" + varb.getName());
                    } else {
                        // instance field
                        node.getQualifier().accept(this);
                        sb.append(getFieldAccessSuffix(
                                node.getQualifier().resolveTypeBinding(),
                                varb.getName()));
                    }
                } else {
                    throw err("Unrecognized QualfiedName type: " + node.getClass(), node);
                }
                return false;
            }

            @Override
            public boolean visit(ThisExpression node) {
                sb.append("this");
                return false;
            }

            @Override
            public boolean visit(VariableDeclarationExpression node) {
                if (node.fragments().size() > 1) {
                    throw err("variable declaration here with more than one fragment is not supported", node);
                }
                for (Object obj : node.fragments()) {
                    VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
                    sb.append("let " + fragment.getName().toString());
                    Expression init = fragment.getInitializer();
                    if (init != null) {
                        sb.append("=");
                        translateExpression(init);
                    }
                }
                return false;
            }

            @Override
            public boolean visit(CastExpression node) {
                ITypeBinding type = node.resolveTypeBinding().getErasure();
                switch (type.getQualifiedName()) {
                    case "java.lang.Object": {
                        // for 'Object' no extra cast is needed
                        // (this can happen with unchecked casts)
                        node.getExpression().accept(this);
                        break;
                    }
                    case "java.lang.String": {
                        sb.append("$STRCAST(");
                        node.getExpression().accept(this);
                        sb.append(")");
                        break;
                    }
                    case "int":
                    case "java.lang.Integer": {
                        if (node.getExpression() instanceof CharacterLiteral) {
                            // a character literal immediately cast to an int.
                            // In this case, let's just replace the expression with
                            // the actual integer value.
                            char ch = (Character) node.getExpression().resolveConstantExpressionValue();
                            sb.append("" + (int) ch);
                        } else if (node.getExpression().resolveTypeBinding().getQualifiedName().equals("char")) {
                            node.getExpression().accept(this);
                            sb.append(".codePointAt(0)");
                        } else {
                            sb.append("$INTCAST(");
                            node.getExpression().accept(this);
                            sb.append(")");
                        }
                        break;
                    }
                    case "double":
                    case "java.lang.Double": {
                        sb.append("$NUMCAST(");
                        node.getExpression().accept(this);
                        sb.append(")");
                        break;
                    }
                    default: {
                        if (type.isInterface()) {
                            // cast to an interface type
                            sb.append("$CASTIF(");
                            node.getExpression().accept(this);
                            sb.append(',');
                            sb.append("'I$" + type.getQualifiedName().replace(".", "$") + "'");
                            sb.append(")");
                        } else {
                            // cast to a class type
                            sb.append("$CASTCLS(");
                            node.getExpression().accept(this);
                            sb.append(',');
                            sb.append(getClassReference(type.getQualifiedName()));
                            sb.append(")");
                        }
                        break;
                    }
                }
                return false;
            }

            @Override
            public boolean visit(InstanceofExpression node) {
                ITypeBinding type = node.getRightOperand().resolveBinding().getErasure();
                String qualifiedName = type.getQualifiedName();
                if (qualifiedName.equals("java.lang.String")) {
                    sb.append("$INSTOFSTR(");
                    translateExpression(node.getLeftOperand());
                    sb.append(")");
                } else if (qualifiedName.equals("java.lang.Double")) {
                    sb.append("$INSTOFNUM(");
                    translateExpression(node.getLeftOperand());
                    sb.append(")");
                } else if (qualifiedName.equals("java.lang.Integer")) {
                    sb.append("$INSTOFINT(");
                    translateExpression(node.getLeftOperand());
                    sb.append(")");
                } else if (qualifiedName.equals("java.lang.Object")) {
                    // must always be true
                    sb.append("(");
                    translateExpression(node.getLeftOperand());
                    sb.append("&&0||true)");
                } else if (qualifiedName.startsWith("crossj.base.Func")) {
                    int argc = Integer.parseInt(qualifiedName.substring("crossj.base.Func".length()));
                    sb.append("$INSTOFFN(");
                    translateExpression(node.getLeftOperand());
                    sb.append("," + argc + ")");
                } else if (type.isInterface()) {
                    // for interface types, we check the tag
                    String tag = "I$" + type.getQualifiedName().replace(".", "$");
                    translateExpression(node.getLeftOperand());
                    sb.append("." + tag);
                } else {
                    // for class types, we use Javascript's own 'instanceof'
                    sb.append("(");
                    translateExpression(node.getLeftOperand());
                    sb.append(" instanceof ");
                    sb.append(getClassReference(type.getQualifiedName()));
                    sb.append(")");
                }
                return false;
            }

            @Override
            public boolean visit(PrefixExpression node) {
                sb.append(node.getOperator());
                translateExpression(node.getOperand());
                return false;
            }

            @Override
            public boolean visit(PostfixExpression node) {
                translateExpression(node.getOperand());
                sb.append(node.getOperator());
                return false;
            }

            @Override
            public boolean visit(InfixExpression node) {
                ITypeBinding type = node.getLeftOperand().resolveTypeBinding();
                if (type.getQualifiedName().equals("int")) {
                    switch (node.getOperator().toString()) {
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                        case "%": {
                            // if we're doing int arithmetic, we need to make sure the results turn out
                            // integers
                            List<Expression> operands = List.of(node.getLeftOperand(), node.getRightOperand());
                            for (Object operand : node.extendedOperands()) {
                                operands.add((Expression) operand);
                            }
                            int intCount = 0;
                            for (int i = 1; i < operands.size(); i++) {
                                if (operands.get(i).resolveTypeBinding().getQualifiedName().equals("int")) {
                                    intCount++;
                                } else {
                                    break;
                                }
                            }
                            sb.append("(");
                            for (int i = 0; i < intCount; i++) {
                                sb.append("((");
                            }
                            translateExpression(operands.get(0));
                            for (int i = 1; i < operands.size(); i++) {
                                handleInfixOperator(node.getOperator());
                                translateExpression(operands.get(i));
                                if (intCount > 0) {
                                    intCount--;
                                    sb.append(")|0)");
                                }
                            }
                            sb.append(")");
                            return false;
                        }
                    }
                }
                sb.append("(");
                translateExpression(node.getLeftOperand());
                handleInfixOperator(node.getOperator());
                translateExpression(node.getRightOperand());
                for (Object operand : node.extendedOperands()) {
                    handleInfixOperator(node.getOperator());
                    translateExpression((Expression) operand);
                }
                sb.append(")");
                return false;
            }

            private void handleInfixOperator(InfixExpression.Operator operator) {
                switch (operator.toString()) {
                    case "==": {
                        sb.append("===");
                        break;
                    }
                    case "!=": {
                        sb.append("!==");
                        break;
                    }
                    default: {
                        sb.append(operator);
                    }
                }
            }

            @Override
            public boolean visit(ConditionalExpression node) {
                sb.append("(");
                translateExpression(node.getExpression());
                sb.append("?");
                translateExpression(node.getThenExpression());
                sb.append(":");
                translateExpression(node.getElseExpression());
                sb.append(")");
                return false;
            }
        });
    }
}
