package com.github.math4tots.crossj.target;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.math4tots.crossj.Parser;

public abstract class Target {
    protected final Parser parser;

    protected Target(Parser parser) {
        this.parser = parser;
    }

    public abstract void emit(List<CompilationUnit> compilationUnits, Optional<String> mainClass, File out);

    protected RuntimeException err(String message, Node... nodes) {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            File file = getFile(node);
            Optional<Position> opos = node.getBegin();
            if (opos.isPresent()) {
                Position pos = opos.get();
                sb.append("In file " + file + " on line " + pos.line + " on column " + pos.column + "\n");
            } else {
                sb.append("In file " + file + "\n");
            }
        }
        sb.append(message + "\n");
        return new RuntimeException(sb.toString());
    }

    protected static void writeFile(File file, String data) {
        try {
            File parent = file.getParentFile();
            parent.mkdirs();
            Files.write(file.toPath(), data.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String getResourceAsString(String path) {
        InputStream inputStream = Target.class.getClassLoader().getResourceAsStream(path);
        try (Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        }
    }

    protected static CompilationUnit getCompilationUnit(Node node) {
        while (!(node instanceof CompilationUnit)) {
            Optional<Node> parent = node.getParentNode();
            if (parent.isPresent()) {
                node = node.getParentNode().get();
            } else {
                throw new RuntimeException("Node outside a CompilationUnit");
            }
        }
        return (CompilationUnit) node;
    }

    protected File getFile(Node node) {
        return parser.getFileForCompilationUnit(getCompilationUnit(node));
    }

    protected String getFullClassName(Node node) {
        return getPackage(node) + "." + getPrimaryTypeSimpleName(node);
    }

    protected String getPackage(Node node) {
        CompilationUnit cu = getCompilationUnit(node);
        Optional<PackageDeclaration> optpkg = cu.getPackageDeclaration();
        if (!optpkg.isPresent()) {
            throw err("Missing package declaration", node);
        }
        return optpkg.get().getNameAsString();
    }

    protected TypeDeclaration<?> getPrimaryType(Node node) {
        CompilationUnit cu = getCompilationUnit(node);
        Optional<TypeDeclaration<?>> type = cu.getPrimaryType();
        if (type.isPresent()) {
            return type.get();
        } else {
            throw err("Missing primary type", node);
        }
    }

    protected String getPrimaryTypeSimpleName(Node node) {
        return getPrimaryType(node).getNameAsString();
    }

    protected boolean isNativeCompilationUnit(CompilationUnit compilationUnit) {
        TypeDeclaration<?> tdecl = getPrimaryType(compilationUnit);
        if (tdecl instanceof ClassOrInterfaceDeclaration) {
            return isNativeClass((ClassOrInterfaceDeclaration) tdecl);
        } else {
            return false;
        }
    }

    protected boolean isNativeClass(ClassOrInterfaceDeclaration node) {
        int nnative = 0;
        int nuser = 0;
        for (MethodDeclaration method: node.getMethods()) {
            if (method.isNative()) {
                nnative++;
            } else {
                nuser++;
            }
        }
        nuser += node.getFields().size();
        if (nnative == 0) {
            return false;
        } else if (nuser == 0) {
            return true;
        } else {
            throw err("A class cannot mix native and non-native methods and fields", node);
        }
    }

    protected ResolvedType getExpressionType(Expression node) {
        try {
            return parser.getSymbolSolver().calculateType(node);
        } catch (RuntimeException e) {
            // we need to potentially modify the expression, to apply a hack
            // to deal with a bug in javaparser
            prepareExpressionForResolution(node);
            return parser.getSymbolSolver().calculateType(node);
        }
    }

    private DataKey<Boolean> PREPARED = new DataKey<Boolean>(){};

    protected void prepareExpressionForResolution(Expression node) {
        if (node.containsData(PREPARED)) {
            // node is already prepared
            return;
        }
        node.walk(TreeTraversal.POSTORDER, child -> {
            if (child instanceof MethodCallExpr) {
                MethodCallExpr methodCall = (MethodCallExpr) child;
                if (methodCall.getScope().isPresent() && methodCall.getScope().get() instanceof NameExpr) {
                    getExpresionTypeForSpecialCase(methodCall);
                }
            }
            if (child instanceof Expression) {
                child.setData(PREPARED, true);
            }
        });
    }

    // Hack for dealing with a bug in javaparser. For more info, See:
    // https://github.com/javaparser/javaparser/issues/2283
    private ResolvedType getExpresionTypeForSpecialCase(MethodCallExpr node) {
        try {
            return parser.getSymbolSolver().calculateType(node);
        } catch (RuntimeException e) {
            NameExpr scope = node.getScope().get().asNameExpr();
            ResolvedType scopeType = getExpressionType(scope);
            String name = scopeType.asReferenceType().getQualifiedName();
            Expression qualifiedScope = new NameExpr(
                scope.getTokenRange().get(),
                new SimpleName(name.substring(0, name.indexOf(".")))
            );
            name = name.substring(name.indexOf(".") + 1);
            while (name.length() > 0) {
                int cut = name.indexOf(".");
                String part;
                if (cut == -1) {
                    part = name;
                    name = "";
                } else {
                    part = name.substring(0, cut);
                    name = name.substring(cut + 1);
                }
                qualifiedScope = new FieldAccessExpr(
                    scope.getTokenRange().get(),
                    qualifiedScope,
                    null,
                    new SimpleName(part));
            }
            node.setScope(qualifiedScope);
            return parser.getSymbolSolver().calculateType(node);
        }
    }

    protected ResolvedType getType(NodeWithType<?, ?> node) {
        JavaSymbolSolver solver = parser.getSymbolSolver();
        Type type = node.getType();
        return solver.toResolvedType(type, ResolvedType.class);
    }
}
