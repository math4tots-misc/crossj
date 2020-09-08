package com.github.math4tots.crossj.target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.resolution.declarations.ResolvedAnnotationDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.math4tots.crossj.Parser;

public final class JavascriptTarget extends Target {
    private final String prelude;
    private final List<TestEntry> tests = new ArrayList<>();

    public JavascriptTarget(Parser parser) {
        super(parser);
        prelude = getResourceAsString("prelude.js");
    }

    @Override
    public void emit(List<CompilationUnit> compilationUnits, Optional<String> mainClass, File out) {
        StringBuilder sb = new StringBuilder();
        sb.append("const $CJ = (function(){\n");
        sb.append(prelude);
        for (CompilationUnit cu : compilationUnits) {
            sb.append(new Translator().translate(cu));
        }
        sb.append("function $listTests(){ return [\n");
        for (TestEntry entry: tests) {
            sb.append("['" + entry.className + "','" + entry.methodName + "'],\n");
        }
        sb.append("];}\n");
        mainClass.ifPresent(mcls -> {
            sb.append(getJSClassRef(mcls) + ".main([]);\n");
        });
        sb.append("return $CJ;\n");
        sb.append("})();");
        File outfile = new File(out, "bundle.js");
        writeFile(outfile, sb.toString());
    }

    private final class TestEntry {
        public final String className;
        public final String methodName;

        public TestEntry(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }

    private final class Translator extends VoidVisitorWithDefaults<Void> {
        private final StringBuilder sb = new StringBuilder();

        private String translate(CompilationUnit cu) {
            visit(cu, null);
            return sb.toString();
        }

        @Override
        public void defaultAction(Node n, Void arg) {
            throw err("Unexpected Node type: " + n.getClass(), n);
        }

        @Override
        public void visit(CompilationUnit n, Void arg) {
            if (isNativeCompilationUnit(n)) {
                // In this case, assume that an implementation is already
                // provided
            } else {
                TypeDeclaration<?> decl = n.getPrimaryType().get();
                if (decl instanceof AnnotationDeclaration) {
                    return;
                }
                sb.append("$CJ['" + getClassKey(getFullClassName(n)) + "'] = $LAZY(function() {\n");
                ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) n.getPrimaryType().get();
                sb.append("return ");
                handleClass(cls);
                sb.append(";\n");
                sb.append("});\n");
            }
        }

        private void handleClass(ClassOrInterfaceDeclaration n) {
            String sname = n.getNameAsString();
            sb.append("class " + sname + " {\n");
            if (n.getConstructors().isEmpty()) {
                // if there are no constructors explicitly listed,
                // just create a default one with fields explicitly set
                sb.append("constructor(){\n");
                for (FieldDeclaration field : n.getFields()) {
                    if (!field.isStatic()) {
                        for (VariableDeclarator fdecl : field.getVariables()) {
                            sb.append("this." + fdecl.getNameAsString() + "=null;\n");
                        }
                    }
                }
                sb.append("}\n");
            } else {
                // there should only ever be at most 1 (checked in validator)
                for (ConstructorDeclaration constructor : n.getConstructors()) {
                    handleConstructor(constructor);
                }
            }
            for (MethodDeclaration method : n.getMethods()) {
                handleMethod(method);
            }
            for (FieldDeclaration field : n.getFields()) {
                if (field.isStatic()) {
                    for (VariableDeclarator vdecl : field.getVariables()) {
                        handleStaticField(vdecl);
                    }
                }
            }
            sb.append("}\n");
        }

        private void handleConstructor(ConstructorDeclaration constructor) {
            sb.append("constructor(");
            boolean first = true;
            for (Parameter parameter : constructor.getParameters()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(parameter.getNameAsString());
                first = false;
            }
            sb.append(") {\n");
            BlockStmt body = constructor.getBody();
            body.accept(this, null);
            sb.append("}");
        }

        private void handleMethod(MethodDeclaration method) {
            if (!method.getBody().isPresent()) {
                return;
            }
            if (method.isStatic()) {
                sb.append("static ");
            }
            for (AnnotationExpr aexpr: method.getAnnotations()) {
                ResolvedAnnotationDeclaration annotation = aexpr.resolve();
                if (annotation.getQualifiedName().equals("crossj.Test")) {
                    if (!method.isStatic()) {
                        throw err("non-static methods cannot be a test", aexpr);
                    }
                    ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) method.getParentNode().get();
                    String className = cls.getFullyQualifiedName().get();
                    String methodName = method.getNameAsString();
                    tests.add(new TestEntry(className, methodName));
                }
            }
            sb.append(method.getNameAsString() + "(");
            boolean first = true;
            for (Parameter parameter : method.getParameters()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(parameter.getNameAsString());
                first = false;
            }
            sb.append(") {\n");
            BlockStmt body = method.getBody().get();
            body.accept(this, null);
            sb.append("}");
        }

        private void handleStaticField(VariableDeclarator decl) {
            decl.getInitializer().ifPresent(init -> {
                sb.append("static " + decl.getNameAsString() + "=");
                init.accept(this, null);
                sb.append(";");
            });
        }

        @Override
        public void visit(BlockStmt n, Void arg) {
            sb.append("{\n");
            for (Statement stmt : n.getStatements()) {
                stmt.accept(this, arg);
            }
            sb.append("}\n");
        }

        @Override
        public void visit(ExpressionStmt n, Void arg) {
            n.getExpression().accept(this, arg);
            if (n.getExpression() instanceof VariableDeclarationExpr) {
                sb.append('\n');
            } else {
                sb.append(";\n");
            }
        }

        @Override
        public void visit(IfStmt n, Void arg) {
            sb.append("if (");
            n.getCondition().accept(this, arg);
            sb.append(") {\n");
            n.getThenStmt().accept(this, arg);
            sb.append("}\n");
            n.getElseStmt().ifPresent(estmt -> {
                if (estmt instanceof IfStmt) {
                    sb.append("else ");
                    estmt.accept(this, arg);
                } else {
                    sb.append("else {");
                    estmt.accept(this, arg);
                    sb.append("}\n");
                }
            });
        }

        @Override
        public void visit(ForStmt n, Void arg) {
            sb.append("for (");
            NodeList<Expression> initlist = n.getInitialization();
            switch (initlist.size()) {
                case 0:
                    sb.append(';');
                    break;
                case 1: {
                    Expression init = initlist.get(0);
                    init.accept(this, arg);
                    if (!(init instanceof VariableDeclarationExpr)) {
                        sb.append(';');
                    }
                    break;
                }
                default: {
                    throw err("Multi-init list in for loops not supported", n);
                }
            }
            n.getCompare().ifPresent(cmp -> cmp.accept(this, arg));
            sb.append(';');
            {
                NodeList<Expression> updatelist = n.getUpdate();
                for (int i = 0; i < updatelist.size(); i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    updatelist.get(i).accept(this, arg);
                }
            }
            sb.append(") {\n");
            n.getBody().accept(this, arg);
            sb.append("}\n");
        }

        @Override
        public void visit(ForEachStmt n, Void arg) {
            sb.append("for (let " + n.getVariableDeclarator().getNameAsString() + " of ");
            n.getIterable().accept(this, arg);
            sb.append("){\n");
            n.getBody().accept(this, arg);
            sb.append("}\n");
        }

        @Override
        public void visit(SwitchStmt n, Void arg) {
            sb.append("switch (");
            Expression selector = n.getSelector();
            selector.accept(this, arg);
            sb.append(") {\n");
            for (SwitchEntry entry : n.getEntries()) {
                NodeList<Expression> labels = entry.getLabels();
                if (labels.isEmpty()) {
                    sb.append("default:");
                } else {
                    for (Expression label : labels) {
                        sb.append("case ");
                        label.accept(this, arg);
                        sb.append(":");
                    }
                }
                sb.append("{\n");
                for (Statement stmt : entry.getStatements()) {
                    stmt.accept(this, arg);
                }
                sb.append("}\n");
            }
            sb.append("}\n");
        }

        @Override
        public void visit(BreakStmt n, Void arg) {
            sb.append("break;\n");
        }

        @Override
        public void visit(ReturnStmt n, Void arg) {
            sb.append("return ");
            n.getExpression().ifPresent(expr -> {
                expr.accept(this, arg);
            });
            sb.append(";\n");
        }

        @Override
        public void visit(ThrowStmt n, Void arg) {
            sb.append("throw ");
            n.getExpression().accept(this, arg);
            sb.append(";\n");
        }

        @Override
        public void visit(TryStmt n, Void arg) {
            sb.append("try ");
            n.getTryBlock().accept(this, arg);
            n.getCatchClauses().forEach(clause -> {
                sb.append("catch($e){\n");
                sb.append("if(!($e instanceof $CJ['crossj.XError']()))throw $e;");
                sb.append("}\n");
            });
            n.getFinallyBlock().ifPresent(fin -> {
                sb.append("finally{\n");
                fin.accept(this, arg);
                sb.append("}\n");
            });
        }

        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            if (n.getVariables().size() > 1) {
                throw err("Multiple variable definitions on one statement not supported", n);
            }
            n.getVariables().forEach(v -> v.accept(this, arg));
        }

        @Override
        public void visit(VariableDeclarator n, Void arg) {
            Optional<Expression> oinit = n.getInitializer();
            sb.append("let " + n.getNameAsString());
            if (oinit.isPresent()) {
                sb.append("=");
                oinit.get().accept(this, arg);
            }
            sb.append(';');
        }

        @Override
        public void visit(NameExpr n, Void arg) {
            ResolvedValueDeclaration decl = n.resolve();
            if (decl.isField()) {
                ResolvedFieldDeclaration field = decl.asField();
                if (field.isStatic()) {
                    String qclsname = field.getType().asReferenceType().getQualifiedName();
                    sb.append(getJSClassRef(qclsname) + "." + field.getName());
                } else {
                    sb.append("this." + field.getName());
                }
            } else {
                sb.append(decl.getName());
            }
        }

        @Override
        public void visit(ThisExpr n, Void arg) {
            sb.append("this");
        }

        @Override
        public void visit(ClassExpr n, Void arg) {
            sb.append(getJSClassRef(n.getType().resolve().asReferenceType().getQualifiedName()));
        }

        @Override
        public void visit(AssignExpr n, Void arg) {
            n.getTarget().accept(this, arg);
            switch (n.getOperator()) {
                case ASSIGN:
                case BINARY_AND:
                case BINARY_OR:
                case PLUS:
                case MINUS:
                case MULTIPLY:
                case REMAINDER: {
                    sb.append(n.getOperator().asString());
                    break;
                }
                default: {
                    throw err("Asissgn operator " + n.getOperator() + " not yet supported", n);
                }
            }
            n.getValue().accept(this, arg);
        }

        @Override
        public void visit(EnclosedExpr n, Void arg) {
            sb.append('(');
            n.getInner().accept(this, arg);
            sb.append(')');
        }

        @Override
        public void visit(ObjectCreationExpr n, Void arg) {
            if (n.getAnonymousClassBody().isPresent()) {
                throw err("Anonymous classes are not supported", n);
            }
            ResolvedConstructorDeclaration constructor = n.resolve();
            sb.append("new (" + getJSClassRef(constructor) + ")");
            emitArgs(constructor, n);
        }

        @Override
        public void visit(FieldAccessExpr n, Void arg) {
            n.getScope().accept(this, arg);
            sb.append("." + n.getNameAsString());
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            ResolvedMethodDeclaration method = n.resolve();

            // Handle some special cases
            switch (method.getQualifiedName()) {
                case "java.lang.String.length": {
                    // For string length, we access the field 'length' instead
                    n.getScope().get().accept(this, arg);
                    sb.append(".length");
                    return;
                }
                case "java.lang.Object.equals": {
                    // We can't be sure whether or not there is an override.
                    sb.append("$EQ(");
                    n.getScope().get().accept(this, arg);
                    sb.append(",");
                    n.getArgument(0).accept(this, arg);
                    sb.append(")");
                    return;
                }
                case "java.lang.Object.hashCode": {
                    // We can't be sure whether or not there is an override.
                    sb.append("$HASH(");
                    n.getScope().get().accept(this, arg);
                    sb.append(")");
                    return;
                }
                case "java.lang.Double.hashCode":
                case "java.lang.Integer.hashCode": {
                    sb.append("$NUMHASH(");
                    n.getScope().get().accept(this, arg);
                    sb.append(")");
                    return;
                }
                case "java.lang.String.hashCode": {
                    sb.append("$STRHASH(");
                    n.getScope().get().accept(this, arg);
                    sb.append(")");
                    return;
                }
                case "java.lang.Class.toString": {
                    sb.append("$CLS2STR(");
                    n.getScope().get().accept(this, arg);
                    sb.append(")");
                    return;
                }
                case "crossj.Func0.apply":
                case "crossj.Func1.apply":
                case "crossj.Func2.apply":
                case "crossj.Func3.apply":
                case "crossj.Func4.apply":
                case "crossj.Func5.apply":
                case "crossj.Func6.apply":
                case "crossj.Func7.apply":
                case "crossj.Func8.apply": {
                    // These are actual javascript functions,
                    // so we need to call them like functions
                    n.getScope().get().accept(this, arg);
                    emitArgs(method, n);
                    return;
                }
            }
            if (method.getQualifiedName().startsWith("crossj.XIterator.")) {
                sb.append("$ITER" + method.getName() + "(");
                n.getScope().get().accept(this, arg);
                for (Expression marg: n.getArguments()) {
                    sb.append(',');
                    marg.accept(this, arg);
                }
                sb.append(')');
                return;
            }

            if (method.isStatic()) {
                sb.append(getJSClassRef(method) + "." + method.getName());
            } else {
                Optional<Expression> oscope = n.getScope();
                if (oscope.isPresent()) {
                    Expression scope = oscope.get();
                    scope.accept(this, arg);
                } else {
                    // if a scope is not present on a non-static method,
                    // an implicit 'this' is assumed
                    sb.append("this");
                }
                sb.append("." + method.getName());
            }
            emitArgs(method, n);
        }

        private void emitArgs(ResolvedMethodLikeDeclaration method, NodeWithArguments<?> call) {
            sb.append("(");
            if (shouldSplatLastArgument(method, call)) {
                // apply a '...' to the last argument
                // Being on this path means that:
                // * the number of parameters and number of arguments match exactly, and
                // * there's at least one parameter (the variadic one)
                // (it follows that there's also at least one argument)
                for (Expression argexpr : call.getArguments().subList(0, call.getArguments().size() - 1)) {
                    argexpr.accept(this, null);
                    sb.append(',');
                }
                sb.append("...");
                call.getArguments().getLast().get().accept(this, null);
            } else {
                // normal function call application without '...'
                boolean first = true;
                for (Expression argexpr : call.getArguments()) {
                    if (!first) {
                        sb.append(",");
                    }
                    first = false;
                    argexpr.accept(this, null);
                }
            }
            sb.append(")");
        }

        private boolean shouldSplatLastArgument(ResolvedMethodLikeDeclaration method, NodeWithArguments<?> call) {
            if (!method.hasVariadicParameter() || call.getArguments().isEmpty()
                    || method.getNumberOfParams() != call.getArguments().size()) {
                return false;
            }
            Expression lastArgument = call.getArguments().getLast().get();
            ResolvedType lastArgumentType = lastArgument.calculateResolvedType();
            return method.getLastParam().getType().equals(lastArgumentType);
        }

        @Override
        public void visit(LambdaExpr n, Void arg) {
            ResolvedType type = getExpressionType(n);
            switch (type.asReferenceType().getQualifiedName()) {
                case "crossj.Func0":
                case "crossj.Func1":
                case "crossj.Func2":
                case "crossj.Func3":
                case "crossj.Func4":
                case "crossj.Func5":
                case "crossj.Func6":
                case "crossj.Func7":
                case "crossj.Func8": {
                    sb.append("((");
                    for (int i = 0; i < n.getParameters().size(); i++) {
                        if (i > 0) {
                            sb.append(',');
                        }
                        Parameter parameter = n.getParameter(i);
                        sb.append(parameter.getNameAsString());
                    }
                    sb.append(")=>");
                    Statement body = n.getBody();
                    if (body.isExpressionStmt()) {
                        Expression expr = body.asExpressionStmt().getExpression();
                        expr.accept(this, arg);
                    } else {
                        sb.append("{\n");
                        n.getBody().accept(this, arg);
                        sb.append("}");
                    }
                    sb.append(')');
                    break;
                }
                default: {
                    // This should already have been caught by ValidatorTarget
                    throw err("Invalid lambda type: " + type.asReferenceType().getQualifiedName(), n);
                }
            }
        }

        @Override
        public void visit(InstanceOfExpr n, Void arg) {
            ResolvedType rawtype = n.getType().resolve();
            if (rawtype.isReferenceType()) {
                ResolvedReferenceType type = rawtype.asReferenceType();
                if (type.getQualifiedName().equals("java.lang.String")) {
                    sb.append("(typeof ");
                    n.getExpression().accept(this, arg);
                    sb.append("==='string')");
                    return;
                }

                sb.append('(');
                n.getExpression().accept(this, arg);
                sb.append(" instanceof ");
                sb.append(getJSClassRef(type));
                sb.append(')');
                return;
            }
            throw err("instanceof for type " + rawtype.describe() + " is not supported", n);
        }

        @Override
        public void visit(CastExpr n, Void arg) {
            ResolvedType rawtype = n.getType().resolve();
            if (rawtype.isReferenceType()) {
                ResolvedReferenceType type = rawtype.asReferenceType();
                if (type.getQualifiedName().equals("java.lang.String")) {
                    sb.append("$STRCAST(");
                    n.getExpression().accept(this, arg);
                    sb.append(")");
                    return;
                }

                sb.append("$CAST(");
                n.getExpression().accept(this, arg);
                sb.append("," + getJSClassRef(type) + ")");
                return;
            }
            throw err("cast to type " + rawtype.describe() + " is not supported", n);
        }

        @Override
        public void visit(UnaryExpr n, Void arg) {
            n.calculateResolvedType();
            switch (n.getOperator()) {
                case BITWISE_COMPLEMENT:
                case MINUS:
                case PLUS:
                case PREFIX_DECREMENT:
                case PREFIX_INCREMENT:
                case LOGICAL_COMPLEMENT: {
                    sb.append("(");
                    sb.append(n.getOperator().asString());
                    n.getExpression().accept(this, arg);
                    sb.append(")");
                    break;
                }
                case POSTFIX_INCREMENT:
                case POSTFIX_DECREMENT: {
                    sb.append("(");
                    n.getExpression().accept(this, arg);
                    sb.append(n.getOperator().asString());
                    sb.append(")");
                    break;
                }
            }
        }

        @Override
        public void visit(BinaryExpr n, Void arg) {
            // when translating to Javascript, the operators mostly align
            ResolvedType restype = n.calculateResolvedType();

            switch (n.getOperator()) {
                case AND:
                case BINARY_AND:
                case BINARY_OR:
                case GREATER_EQUALS:
                case GREATER:
                case LEFT_SHIFT:
                case LESS_EQUALS:
                case LESS:
                case MINUS:
                case MULTIPLY:
                case OR:
                case PLUS:
                case REMAINDER:
                case SIGNED_RIGHT_SHIFT:
                case UNSIGNED_RIGHT_SHIFT:
                case XOR: {
                    sb.append("(");
                    n.getLeft().accept(this, arg);
                    sb.append(n.getOperator().asString());
                    n.getRight().accept(this, arg);
                    sb.append(")");
                    break;
                }
                case EQUALS: {
                    sb.append("(");
                    n.getLeft().accept(this, arg);
                    sb.append("===");
                    n.getRight().accept(this, arg);
                    sb.append(")");
                    break;
                }
                case NOT_EQUALS: {
                    sb.append("(");
                    n.getLeft().accept(this, arg);
                    sb.append("!==");
                    n.getRight().accept(this, arg);
                    sb.append(")");
                    break;
                }
                case DIVIDE: {
                    if (restype.equals(ResolvedPrimitiveType.INT)) {
                        sb.append("((");
                        n.getLeft().accept(this, arg);
                        sb.append("/");
                        n.getRight().accept(this, arg);
                        sb.append(")|0)");
                    } else {
                        sb.append("(");
                        n.getLeft().accept(this, arg);
                        sb.append("/");
                        n.getRight().accept(this, arg);
                        sb.append(")");
                    }
                    break;
                }
            }
        }

        @Override
        public void visit(NullLiteralExpr n, Void arg) {
            sb.append("null");
        }

        @Override
        public void visit(BooleanLiteralExpr n, Void arg) {
            sb.append(n.getValue() ? "true" : "false");
        }

        @Override
        public void visit(IntegerLiteralExpr n, Void arg) {
            sb.append(Integer.parseInt(n.getValue()));
        }

        @Override
        public void visit(DoubleLiteralExpr n, Void arg) {
            sb.append(Double.parseDouble(n.getValue()));
        }

        @Override
        public void visit(CharLiteralExpr n, Void arg) {
            // sb.append("'" + reprstrbody(n.getValue()) + "'");
            sb.append("'" + n.getValue() + "'");
        }

        @Override
        public void visit(StringLiteralExpr n, Void arg) {
            // String value = n.getValue();
            // sb.append('"'+ reprstrbody(value) + '"');
            sb.append('"' + n.getValue() + '"');
        }

        @SuppressWarnings("unused")
        private String reprstrbody(String value) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                switch (c) {
                    case '\n': {
                        sb.append("\\n");
                        break;
                    }
                    case '\r': {
                        sb.append("\\r");
                        break;
                    }
                    case '\t': {
                        sb.append("\\t");
                        break;
                    }
                    case '\0': {
                        sb.append("\\0");
                        break;
                    }
                    case '\'': {
                        sb.append("\\\'");
                        break;
                    }
                    case '\"': {
                        sb.append("\\\"");
                        break;
                    }
                    case '\\': {
                        sb.append("\\\\");
                        break;
                    }
                    default: {
                        sb.append(c);
                        break;
                    }
                }
            }
            return sb.toString();
        }
    }

    private static String getClassKey(String qualifiedClassName) {
        return qualifiedClassName;
    }

    private static String getClassKey(String packageName, String className) {
        return getClassKey(packageName + "." + className);
    }

    private static String getJSClassRefFromKey(String key) {
        return "$CJ['" + key + "']()";
    }

    private static String getJSClassRef(String packageName, String className) {
        return getJSClassRefFromKey(getClassKey(packageName, className));
    }

    private static String getJSClassRef(String qualifiedClassName) {
        return getJSClassRefFromKey(getClassKey(qualifiedClassName));
    }

    private static String getJSClassRef(ResolvedMethodLikeDeclaration method) {
        return getJSClassRef(method.getPackageName(), method.getClassName());
    }

    private static String getJSClassRef(ResolvedReferenceType type) {
        return getJSClassRef(type.getQualifiedName());
    }
}
