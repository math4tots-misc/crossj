package com.github.math4tots.crossj;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;

import crossj.XError;

public final class Parser {
    private final TypeSolver typeSolver;
    private final JavaSymbolSolver symbolSolver;
    private final ParserConfiguration parserConfiguration;
    private final JavaParser javaParser;
    private final IdentityHashMap<CompilationUnit, File> originalFiles = new IdentityHashMap<>();

    public static Parser fromSourceRootStrings(List<String> roots) {
        List<File> files = new ArrayList<>();
        for (String root : roots) {
            files.add(new File(root));
        }
        return fromSourceRoots(files);
    }

    public static Parser fromSourceRoots(List<File> roots) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        for (File root : roots) {
            combinedTypeSolver.add(new JavaParserTypeSolver(root));
        }
        return new Parser(combinedTypeSolver);
    }

    private Parser(TypeSolver typeSolver) {
        this.typeSolver = typeSolver;
        this.symbolSolver = new JavaSymbolSolver(typeSolver);
        this.parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(LanguageLevel.JAVA_8);
        // parserConfiguration.setLanguageLevel(LanguageLevel.JAVA_14);
        parserConfiguration.setSymbolResolver(symbolSolver);
        this.javaParser = new JavaParser(parserConfiguration);
    }

    public TypeSolver getTypeSolver() {
        return typeSolver;
    }

    public File getFileForCompilationUnit(CompilationUnit cu) {
        return originalFiles.get(cu);
    }

    public JavaSymbolSolver getSymbolSolver() {
        return symbolSolver;
    }

    /**
     * Make some modifications to the tree to make type inference a bit easier for
     * javaparser.
     *
     * <li>Wrap all lambda expressions in Func*.of(..)</li>
     */
    public void prepareTree(CompilationUnit compilationUnit) {
        compilationUnit.findAll(LambdaExpr.class).forEach(lexpr -> {
            Node originalParent = lexpr.getParentNode().get();

            int argc = lexpr.getParameters().size();

            TokenRange tokenRange = lexpr.getTokenRange().get();
            NameExpr crossjName = new NameExpr(tokenRange, new SimpleName("crossj"));

            FieldAccessExpr funcClassName = new FieldAccessExpr(tokenRange, crossjName, null,
                    new SimpleName("Func" + argc));
            crossjName.setParentNode(funcClassName);

            MethodCallExpr wrappedLambda = new MethodCallExpr(tokenRange, funcClassName, null, new SimpleName("of"),
                    new NodeList<>());
            funcClassName.setParentNode(wrappedLambda);

            if (!lexpr.replace(wrappedLambda)) {
                throw XError.withMessage("Failed to replace lambda expression");
            }

            wrappedLambda.getArguments().add(lexpr);
            lexpr.setParentNode(wrappedLambda);
            wrappedLambda.setParentNode(originalParent);
        });
    }

    public CompilationUnit parseFile(File file) {
        try {
            ParseResult<CompilationUnit> result = javaParser.parse(file);
            if (result.isSuccessful()) {
                CompilationUnit compilationUnit = result.getResult().get();
                // prepareTree(compilationUnit);
                originalFiles.put(compilationUnit, file);
                return compilationUnit;
            } else {
                throw new RuntimeException(result.toString());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException(fileNotFoundException);
        }
    }

    public List<CompilationUnit> parseAllFiles(List<File> files) {
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        for (File file : files) {
            compilationUnits.add(parseFile(file));
        }
        return compilationUnits;
    }

    public List<CompilationUnit> parseAllRoots(List<File> roots) {
        List<File> files = new ArrayList<>();
        for (File root : roots) {
            files.addAll(findAllFiles(root));
        }
        return parseAllFiles(files);
    }

    /**
     * Recursively find and parse all files in a directory
     */
    public List<CompilationUnit> parseAll(File directory) {
        return parseAllFiles(findAllFiles(directory));
    }

    private static List<File> findAllFiles(File directory) {
        List<File> stack = new ArrayList<>(Arrays.asList(directory));
        List<File> out = new ArrayList<>();
        while (!stack.isEmpty()) {
            File file = stack.remove(stack.size() - 1);
            if (file.isDirectory()) {
                stack.addAll(Arrays.asList(file.listFiles()));
            } else if (file.isFile()) {
                String name = file.getName();
                if (name.endsWith(".java") || name.endsWith(".crossj")) {
                    out.add(file);
                }
            }
        }
        return out;
    }
}
