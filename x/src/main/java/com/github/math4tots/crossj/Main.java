package com.github.math4tots.crossj;

import java.io.File;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;

import crossj.IO;
import crossj.List;
import crossj.M;
import crossj.Optional;
import crossj.Pair;
import crossj.Time;
import crossj.XError;

public final class Main {
    public static void main(String[] args) {
        Mode mode = Mode.Default;
        List<String> sourceRoots = List.of();
        Optional<ITranslator> translator = Optional.empty();
        Optional<String> main = Optional.empty();
        Optional<String> outdir = Optional.empty();
        boolean verbose = false;

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
                        case "-v":
                        case "--verbose": {
                            verbose = true;
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
                        case "JavaScript":
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

        List<String> filepaths = findAllFilesInMultipleDirectories(sourceRoots);
        if (verbose) {
            IO.println("Found " + filepaths.size() + " files");
        }

        // Parse all the files
        double parsingStart = Time.now();
        List<Pair<String, CompilationUnit>> compilationUnits = List.fromIterable(parser.parseFiles(filepaths));
        double parsingEnd = Time.now();
        if (verbose) {
            double rounded = M.round(10000 * (parsingEnd - parsingStart)) / 10000.0;
            IO.println("Finished parsing (in " + rounded + "s)");
        }

        // check for problems detected by the parser
        for (int i = 0; i < compilationUnits.size(); i++) {
            var compilationUnit = compilationUnits.get(i).get2();
            StringBuilder sb = new StringBuilder();
            for (IProblem problem : compilationUnit.getProblems()) {
                if (problem.isError()) {
                    String filename = new String(problem.getOriginatingFileName());
                    sb.append("\nin " + filename + " on line " + problem.getSourceLineNumber() + ": " + problem);
                } else {
                    if (verbose) {
                        String filename = new String(problem.getOriginatingFileName());
                        IO.eprintln("in " + filename + " on line " + problem.getSourceLineNumber() + ": " + problem);
                    }
                }
            }
            String message = sb.toString();
            if (!message.isEmpty()) {
                throw XError.withMessage("\n" + message);
            }
        }

        // do the actual translations
        var translationStart = Time.now();
        for (int i = 0; i < compilationUnits.size(); i++) {
            double start = Time.now();
            var filepath = compilationUnits.get(i).get1();
            var compilationUnit = compilationUnits.get(i).get2();
            if (verbose) {
                IO.print("Translating " + filepath + " (" + (i + 1) + "/" + filepaths.size() + ") ... ");
            }
            tr.translate(filepath, compilationUnit);
            if (verbose) {
                double end = Time.now();
                double rounded = M.round(10000 * (end - start)) / 10000.0;
                IO.println("DONE (in " + rounded + "s)");
            }
        }
        tr.commit();
        var translationEnd = Time.now();
        if (verbose) {
            double rounded = M.round(10000 * (translationEnd - translationStart)) / 10000.0;
            IO.println("All translations DONE (in " + rounded + "s)");
        }
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
