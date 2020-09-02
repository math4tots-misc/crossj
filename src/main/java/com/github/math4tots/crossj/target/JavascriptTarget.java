package com.github.math4tots.crossj.target;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
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
            for (Statement stmt: n.getStatements()) {
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
        public void visit(MethodCallExpr n, Void arg) {
            ResolvedMethodDeclaration method = n.resolve();
            if (method.isStatic()) {
                String key = method.getPackageName() + "." + method.getClassName();
                sb.append("$CLS('" + key + "')." + method.getName() + "(");
                boolean first = true;
                for (Expression argexpr: n.getArguments()) {
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
