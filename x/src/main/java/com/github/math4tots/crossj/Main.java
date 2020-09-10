package com.github.math4tots.crossj;

import java.io.File;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;

import crossj.List;
import crossj.Optional;
import crossj.XError;

public final class Main {
    public static void main(String[] args) {
        Mode mode = Mode.Default;
        List<String> sourceRoots = List.of();
        Optional<ITranslator> translator = Optional.empty();
        Optional<String> main = Optional.empty();
        Optional<String> outdir = Optional.empty();

        for (String arg : args) {
            switch (mode) {
                case Default: {
                    switch (arg) {
                        case "-r":
                        case "--root": {
                            mode = Mode.Root;
                            break;
                        }
                        case "-m":
                        case "--main": {
                            mode = Mode.Main;
                            break;
                        }
                        case "-t":
                        case "--target": {
                            mode = Mode.Target;
                            break;
                        }
                        case "-o":
                        case "--out": {
                            mode = Mode.Out;
                            break;
                        }
                        default: {
                            throw new RuntimeException("Unrecognized option " + arg);
                        }
                    }
                    break;
                }
                case Root: {
                    sourceRoots.add(arg);
                    mode = Mode.Default;
                    break;
                }
                case Main: {
                    main = Optional.of(arg);
                    mode = Mode.Default;
                    break;
                }
                case Target: {
                    switch (arg) {
                        case "javascript":
                        case "Javascript":
                        case "js":
                        case "JS": {
                            translator = Optional.of(new JavascriptTranslator());
                            break;
                        }
                        default: {
                            throw new RuntimeException("Unrecognized target name " + arg);
                        }
                    }
                    mode = Mode.Default;
                    break;
                }
                case Out: {
                    outdir = Optional.of(arg);
                    mode = Mode.Default;
                    break;
                }
            }
        }

        ITranslator tr = translator.isPresent() ? translator.get() : new JavascriptTranslator();
        if (main.isPresent()) {
            tr.setMain(main.get());
        }
        tr.setOutputDirectory(outdir.isPresent() ? outdir.get() : "out");

        Parser parser = new Parser();
        for (String root : sourceRoots) {
            parser.addSourceRoot(root);
        }

        for (String filepath : findAllFilesInMultipleDirectories(sourceRoots)) {
            CompilationUnit compilationUnit = parser.parseFile(filepath);
            StringBuilder sb = new StringBuilder();
            for (IProblem problem : compilationUnit.getProblems()) {
                String filename = new String(problem.getOriginatingFileName());
                sb.append("in " + filename + " on line " + problem.getSourceLineNumber() + ": " + problem);
            }
            String message = sb.toString();
            if (!message.isEmpty()) {
                throw XError.withMessage("\n" + message);
            }
            tr.translate(filepath, compilationUnit);
        }
        tr.commit();
    }

    private enum Mode {
        Default, Root, Main, Target, Out,
    }

    private static List<String> findAllFilesInMultipleDirectories(Iterable<String> directories) {
        List<String> out = List.of();
        for (String directory : directories) {
            out.addAll(findAllFiles(directory));
        }
        return out;
    }

    private static List<String> findAllFiles(String directory) {
        List<File> stack = List.of(new File(directory));
        List<String> out = List.of();
        while (stack.size() > 0) {
            File file = stack.pop();
            if (file.isDirectory()) {
                stack.addAll(List.of(file.listFiles()));
            } else if (file.isFile()) {
                String name = file.getName();
                if (name.endsWith(".java") || name.endsWith(".crossj")) {
                    out.add(file.toString());
                }
            }
        }
        return out;
    }
}
