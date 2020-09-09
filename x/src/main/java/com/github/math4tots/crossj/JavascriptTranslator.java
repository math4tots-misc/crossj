package com.github.math4tots.crossj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
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

import crossj.IO;
import crossj.List;
import crossj.Optional;
import crossj.Pair;
import crossj.XError;

public final class JavascriptTranslator implements ITranslator {
    private String outputDirectory;
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
        outputDirectory = path;
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

    @Override
    public void commit() {
        sb.append("const $TESTS=[\n");
        for (Pair<String, String> test : tests) {
            sb.append("['" + test.get1() + "','" + test.get2() + "'],\n");
        }
        sb.append("];\n");
        if (main.isPresent()) {
            String m = main.get();
            sb.append("$CJ['" + m + "']().main([]);\n");
        }
        sb.append("return $CJ;\n");
        sb.append("})();\n");
        if (!Files.isDirectory(Paths.get(outputDirectory))) {
            try {
                Files.createDirectories(Paths.get(outputDirectory));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String outputFile = outputDirectory + File.separator + "bundle.js";
        IO.writeFile(outputFile, sb.toString());
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
        String name = declaration.getName().toString();
        String qualifiedName = ITranslator.getQualifiedName(declaration);
        sb.append("$CJ['" + qualifiedName + "']=$LAZY(function(){\n");
        sb.append("class " + name + "{\n");
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
        for (MethodDeclaration method : declaration.getMethods()) {
            translateMethod(method);
        }
        sb.append("}\n");
        for (FieldDeclaration field : declaration.getFields()) {
            translateField(field);
        }
        sb.append("return " + name + ";\n");
        sb.append("});\n");
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
                default:
                    throw err("Unrecognized primitive type: " + type.getQualifiedName(), nodes);
            }
        } else {
            return "null";
        }
    }

    public void translateField(FieldDeclaration declaration) {
        String shortClassName = currentTypeDeclaration.getName().toString();
        for (Object obj : declaration.fragments()) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) obj;
            if (Modifier.isStatic(declaration.getModifiers())) {
                // static field
                Expression init = fragment.getInitializer();
                sb.append(shortClassName + ".F$" + fragment.getName() + "=");
                if (init != null) {
                    translateExpression(init);
                } else {
                    sb.append(getDefaultValueForType(declaration.getType().resolveBinding(), fragment));
                }
                sb.append(";\n");
            } else {
                // instance field
                // do nothing here -- this is handled in the constructor
            }
        }
    }

    public String getClassReference(String qualifiedClassName) {
        if (qualifiedClassName.equals(ITranslator.getQualifiedName(currentTypeDeclaration))) {
            return currentTypeDeclaration.getName().toString();
        } else {
            return "$CJ['" + qualifiedClassName + "']()";
        }
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
                            if (binding.getAnnotationType().getQualifiedName().equals("crossj.Test")) {
                                tests.add(Pair.of(currentTypeDeclarationBinding.getQualifiedName(), name));
                            }
                        }
                    }
                });
            }
            sb.append(name);
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

    public void translateExpression(Expression expression) {
        expression.accept(new DefaultVisitor() {
            @Override
            public boolean visit(MethodInvocation node) {
                IMethodBinding method = node.resolveMethodBinding();
                if (Modifier.isStatic(method.getModifiers())) {
                    // static method call
                    ITypeBinding cls = method.getDeclaringClass().getErasure();
                    String qualifiedClassName = cls.getQualifiedName();
                    sb.append(getClassReference(qualifiedClassName) + "." + method.getName() + "(");
                    boolean first = true;
                    for (Object argobj : node.arguments()) {
                        if (!first) {
                            sb.append(',');
                        }
                        first = false;
                        Expression arg = (Expression) argobj;
                        arg.accept(this);
                    }
                    sb.append(")");
                } else {
                    // instance method call
                    String qualifiedClassName = method.getDeclaringClass().getErasure().getQualifiedName();
                    String qualifiedName = qualifiedClassName + "." + method.getName();
                    Expression owner = node.getExpression();

                    if (qualifiedClassName.equals("crossj.XIterator")) {
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
                                translateExpression(owner);
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
                            default: {
                                // map to a corresponding javascript method call
                                if (owner == null) {
                                    sb.append("this");
                                } else {
                                    translateExpression(owner);
                                }
                                sb.append("." + node.getName().toString() + "(");
                                boolean first = true;
                                for (Object argobj : node.arguments()) {
                                    if (!first) {
                                        sb.append(',');
                                    }
                                    first = false;
                                    Expression arg = (Expression) argobj;
                                    arg.accept(this);
                                }
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
                String expectedTypeName = "crossj.Func" + node.parameters().size();
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
                sb.append(node.getToken());
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
                    node.getExpression().accept(this);
                    sb.append(".F$" + varb.getName());
                }
                return false;
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
                        translateExpression(node.getQualifier());
                        sb.append(".F$" + varb.getName());
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
                if (type.getQualifiedName().equals("java.lang.String")) {
                    sb.append("$STRCAST(");
                    node.getExpression().accept(this);
                    sb.append(")");
                } else {
                    sb.append("$CAST(");
                    node.getExpression().accept(this);
                    sb.append(',');
                    sb.append(getClassReference(type.getQualifiedName()));
                    sb.append(")");
                }
                return false;
            }

            @Override
            public boolean visit(InstanceofExpression node) {
                ITypeBinding type = node.getRightOperand().resolveBinding().getErasure();
                if (type.getQualifiedName().equals("java.lang.String")) {
                    sb.append("$INSTOFSTR(");
                    translateExpression(node.getLeftOperand());
                    sb.append(")");
                } else {
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
                translateExpression(node.getLeftOperand());
                handleInfixOperator(node.getOperator());
                translateExpression(node.getRightOperand());
                for (Object operand : node.extendedOperands()) {
                    handleInfixOperator(node.getOperator());
                    translateExpression((Expression) operand);
                }
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
