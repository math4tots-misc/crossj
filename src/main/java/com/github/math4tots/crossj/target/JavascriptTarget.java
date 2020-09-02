package com.github.math4tots.crossj.target;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.math4tots.crossj.Parser;

public final class JavascriptTarget extends Target {
    private final String prelude;

    public JavascriptTarget(Parser parser) {
        super(parser);
        prelude = getResourceAsString("prelude.js");
    }

    @Override
    public void emit(List<CompilationUnit> compilationUnits, Optional<String> mainClass, File out) {
        StringBuilder sb = new StringBuilder();
        sb.append(prelude);
        for (CompilationUnit cu : compilationUnits) {
            sb.append(new Translator().translate(cu));
        }
        mainClass.ifPresent(mcls -> {
            sb.append("$CLS('" + mcls + "').main()\n");
        });
        File outfile = new File(out, "bundle.js");
        writeFile(outfile, sb.toString());
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
                sb.append("$CJ['" + getFullClassName(n) + "'] = $LAZY(function() {\n");
                ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) n.getPrimaryType().get();
                handleClass(cls);
                sb.append("return " + cls.getNameAsString() + ";\n");
                sb.append("});\n");
            }
        }

        private void handleClass(ClassOrInterfaceDeclaration n) {
            String sname = n.getNameAsString();
            sb.append("class " + sname + " {\n");
            for (MethodDeclaration method : n.getMethods()) {
                if (method.isStatic()) {
                    handleStaticMethod(method);
                }
            }
            sb.append("}\n");
        }

        private void handleStaticMethod(MethodDeclaration method) {
            sb.append("static " + method.getNameAsString() + "(");
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
            sb.append(";\n");
        }

        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
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
            sb.append(decl.getName());
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            n.calculateResolvedType();
            ResolvedMethodDeclaration method = n.resolve();
            if (method.isStatic()) {
                String key = method.getPackageName() + "." + method.getClassName();
                sb.append("$CLS('" + key + "')." + method.getName() + "(");
                boolean first = true;
                for (Expression argexpr : n.getArguments()) {
                    if (!first) {
                        sb.append(",");
                    }
                    argexpr.accept(this, arg);
                }
                sb.append(")");
            } else {
                throw err("TODO: non-static method calls", n);
            }
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
        public void visit(IntegerLiteralExpr n, Void arg) {
            sb.append(Integer.parseInt(n.getValue()));
        }

        @Override
        public void visit(DoubleLiteralExpr n, Void arg) {
            sb.append(Double.parseDouble(n.getValue()));
        }

        @Override
        public void visit(StringLiteralExpr n, Void arg) {
            String value = n.getValue();
            sb.append('"');
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
            sb.append('"');
        }
    }
}
