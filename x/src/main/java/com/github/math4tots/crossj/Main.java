package com.github.math4tots.crossj;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;

import crossj.base.IO;
import crossj.base.List;
import crossj.base.M;
import crossj.base.Map;
import crossj.base.Num;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Set;
import crossj.base.Str;
import crossj.base.Time;
import crossj.base.XError;

public final class Main {
    public static void main(String[] args) {
        Mode mode = Mode.Default;
        List<String> sourceRoots = List.of();
        Optional<ITranslator> translator = Optional.empty();
        Optional<String> main = Optional.empty();
        Optional<String> outdir = Optional.empty();
        boolean verboseToggle = false;
        boolean prune = true;

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
                        case "--no-prune": {
                            prune = false;
                            break;
                        }
                        case "-v":
                        case "--verbose": {
                            verboseToggle = true;
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
                        case "web": {
                            translator = Optional.of(new WebTranslator());
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

        // like verboseToggle, but final
        final var verbose = verboseToggle;

        ITranslator tr = translator.isPresent() ? translator.get() : new JavascriptTranslator();
        if (main.isPresent()) {
            tr.setMain(main.get());
        }
        tr.setOutputDirectory(outdir.isPresent() ? outdir.get() : "out");

        Parser parser = new Parser();
        for (String root : sourceRoots) {
            parser.addSourceRoot(root);
        }

        List<String> filepaths;
        if (prune && main.isPresent()) {
            /**
             * If a main class is specified, we can do a very coarse pruning where we only
             * look at packages that are actually needed by that main class and its
             * dependencies.
             */
            var classFileMap = getFilesByPackageName(sourceRoots);
            var fileCount = classFileMap.values().fold(0, (total, list) -> total + list.size());
            if (verbose) {
                IO.println("Found " + classFileMap.size() + " packages containing " + fileCount + " files");
            }

            var pruneStart = Time.now();
            filepaths = filterToDependentPackages(classFileMap, main.get());
            var pruneEnd = Time.now();

            if (verbose) {
                IO.println("Pruned to " + filepaths.size() + " files (in " + Num.format(pruneEnd - pruneStart) + "s)");
            }
        } else {
            /**
             * If either pruning is disabled or no main class is specified, we translate all
             * files we find in these source roots.
             */
            filepaths = findAllFilesInMultipleDirectories(sourceRoots);

            if (verbose) {
                IO.println("Found " + filepaths.size() + " files");
            }
        }

        // Parse all the files
        double parsingStart = Time.now();
        var updateInfo = new Object() {
            public double lastTime = Time.now();
            public double lastRatio = -1;
        };
        List<Pair<String, CompilationUnit>> compilationUnits = List.fromIterable(parser.parseFiles(filepaths, r -> {
            if (verbose) {
                // Update every 1 second if we've made at least 1% progress
                var now = Time.now();
                if (now - updateInfo.lastTime > 1 && r - updateInfo.lastRatio > 0.01) {
                    updateInfo.lastTime = now;
                    updateInfo.lastRatio = r;
                    IO.println("... parsed " + ((int) (r * 100)) + "%");
                }
            }
            return null;
        }));
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

        // Sort compilation units by the number of interfaces they implement (including
        // all implicitly implemented).
        // This ensures that all base types are translated before any given type.
        {
            var cache = new HashMap<ITypeBinding, HashSet<ITypeBinding>>();
            compilationUnits.sortBy((a, b) -> Pair
                    .of(getAllInterfaces(((AbstractTypeDeclaration) a.get2().types().get(0)).resolveBinding(), cache)
                            .size(), a.get1())
                    .compareTo(Pair
                            .of(getAllInterfaces(((AbstractTypeDeclaration) b.get2().types().get(0)).resolveBinding(),
                                    cache).size(), b.get1())));
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

    private static String getPackageName(String qualifiedClassName) {
        return qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf("."));
    }

    private static Map<String, List<String>> getFilesByPackageName(Iterable<String> sourceRoots) {
        Map<String, List<String>> out = Map.of();
        for (var sourceRoot : sourceRoots) {
            var root = Path.of(sourceRoot);
            for (var path : findAllFiles(sourceRoot)) {
                var relativePath = root.relativize(Path.of(path)).toString();
                var qualifiedClassName = relativePathToQualifiedClassName(relativePath);
                var packageName = getPackageName(qualifiedClassName);
                if (!out.containsKey(packageName)) {
                    out.put(packageName, List.of());
                }
                out.get(packageName).add(path);
            }
        }
        return out;
    }

    private static List<String> filterToDependentPackages(Map<String, List<String>> map, String main) {
        List<String> out = List.of();
        var stack = List.of(getPackageName(main));
        var seen = Set.fromIterable(stack);
        while (stack.size() > 0) {
            var packageName = stack.pop();
            for (var path : map.get(packageName)) {
                out.add(path);
                for (var importedPackage : getImportedPackages(path)) {
                    if (!seen.contains(importedPackage)) {
                        seen.add(importedPackage);
                        stack.add(importedPackage);
                    }
                }
            }
        }
        return out;
    }

    private static List<String> getImportedPackages(String path) {
        return Str.lines(IO.readFile(path)).iter().filter(line -> line.startsWith("import "))
                .map(line -> line.substring("import ".length(), line.indexOf(";")))
                .map(className -> getPackageName(className)).list();
    }

    private static HashSet<ITypeBinding> getAllInterfaces(ITypeBinding tb,
            HashMap<ITypeBinding, HashSet<ITypeBinding>> cache) {
        tb = tb.getErasure();
        if (!cache.containsKey(tb)) {
            var ifaces = tb.getInterfaces();
            var set = new HashSet<ITypeBinding>();
            for (var iface : ifaces) {
                iface = iface.getErasure();
                set.add(iface);
                set.addAll(getAllInterfaces(iface, cache));
            }
            cache.put(tb, set);
        }
        return cache.get(tb);
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

    private static String relativePathToQualifiedClassName(String path) {
        if (path.endsWith(".java")) {
            path = path.substring(0, path.length() - ".java".length());
        } else if (path.endsWith(".crossj")) {
            path = path.substring(0, path.length() - ".crossj".length());
        }
        return path.replace("/", ".");
    }
}
