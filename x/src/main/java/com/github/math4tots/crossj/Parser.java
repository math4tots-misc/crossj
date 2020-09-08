package com.github.math4tots.crossj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public final class Parser {
    private final ASTParser parser = ASTParser.newParser(AST.JLS14);
    private final List<String> sources = new ArrayList<>();

    public CompilationUnit parseFile(String path) {
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        java.util.Map<String, String> options = JavaCore.getOptions();
        options.put("org.eclipse.jdt.core.compiler.source", "10");
        parser.setCompilerOptions(options);
        String[] classpath = {};
        String[] sources = new String[this.sources.size()];
        for (int i = 0; i < sources.length; i++) {
            sources[i] = this.sources.get(i);
        }
        parser.setEnvironment(classpath, sources, getEncodings(sources.length), false);

        parser.setUnitName(path);
        parser.setSource(readFile(path).toCharArray());

        return (CompilationUnit) parser.createAST(null);
    }

    public void addSourceRoot(String path) {
        sources.add(path);
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        CompilationUnit cu = (CompilationUnit) parser.parseFile("src/main/java/com/github/math4tots/crossj/Main.java");

        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodInvocation node) {
                String name = node.getName().getFullyQualifiedName();
                ITypeBinding type = node.resolveTypeBinding();
                if (type != null) {
                    System.out.println("method call (" + name + ") -> returns: " + type.getQualifiedName());
                }
                IMethodBinding method = node.resolveMethodBinding();
                if (method != null) {
                    System.out.println("method call (" + name + ") -> found: "
                            + Arrays.asList(method.getMethodDeclaration().getParameterTypes()).stream()
                                    .map(m -> m.getQualifiedName()).collect(Collectors.toList()));
                }
                return super.visit(node);
            }
        });

        // System.out.println("cu = " + cu);
    }

    private static String[] getEncodings(int n) {
        String[] encodings = new String[n];
        for (int i = 0; i < n; i++) {
            encodings[i] = "UTF-8";
        }
        return encodings;
    }

    static String readFile(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
