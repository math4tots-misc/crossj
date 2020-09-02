package com.github.math4tots.crossj;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.math4tots.crossj.target.JavascriptTarget;
import com.github.math4tots.crossj.target.Target;
import com.github.math4tots.crossj.target.ValidatorTarget;

public final class Main {
    public static void main(String[] args) {
        Args xargs = Args.fromStrings(args);
        Parser parser = Parser.fromSourceRoots(xargs.classPaths);
        List<CompilationUnit> compilationUnits = parser.parseAllRoots(xargs.roots);
        new ValidatorTarget(parser).emit(compilationUnits, xargs.mainClass, null);
        Target target;
        switch (xargs.target) {
            case "javascript":
            case "Javascript":
            case "JavaScript":
            case "js":
            case "JS": {
                target = new JavascriptTarget(parser);
                break;
            }
            default: {
                throw new RuntimeException("Unrecognized target: " + xargs.target);
            }
        }
        target.emit(compilationUnits, xargs.mainClass, xargs.out);
    }
}
