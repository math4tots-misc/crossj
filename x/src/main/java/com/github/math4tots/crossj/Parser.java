package com.github.math4tots.crossj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import crossj.Assert;
import crossj.Func1;
import crossj.Pair;

public final class Parser {
    private final ASTParser parser = ASTParser.newParser(AST.JLS14);
    private final List<String> sources = new ArrayList<>();

    public List<Pair<String, CompilationUnit>> parseFiles(Iterable<String> pathsAsIterable,
            Func1<Void, Double> progressMonitor) {
        List<String> paths = new ArrayList<>();
        pathsAsIterable.forEach(path -> paths.add(path));
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

        parser.setUnitName(paths.get(0));
        parser.setSource(readFile(paths.get(0)).toCharArray());

        List<Pair<String, CompilationUnit>> cus = new ArrayList<>();
        parser.createASTs(toStringArray(paths), getEncodings(paths.size()), new String[0], new FileASTRequestor() {
            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                cus.add(Pair.of(sourceFilePath, ast));
            }
        }, new NullProgressMonitor() {
            int totalWork = 0;
            int doneSoFar = 0;

            public void beginTask(String name, int totalWork) {
                this.totalWork = totalWork;
            };

            public void worked(int work) {
                Assert.withMessage(totalWork != 0, "Tried to report progress when total work is 0");
                doneSoFar += work;
                if (progressMonitor != null) {
                    progressMonitor.apply(doneSoFar / (double) totalWork);
                }
            };
        });

        return cus;
    }

    private static String[] toStringArray(List<String> strings) {
        var ret = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            ret[i] = strings.get(i);
        }
        return ret;
    }

    public CompilationUnit parseFile(String path) {
        return parseFiles(Arrays.asList(path), null).get(0).get2();
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
