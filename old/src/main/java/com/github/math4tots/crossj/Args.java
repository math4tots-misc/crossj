package com.github.math4tots.crossj;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class Args {
    public final List<File> roots;
    public final File out;
    public final String target;
    public final List<File> classPaths;
    public final Optional<String> mainClass;

    private Args(List<File> roots, Optional<String> mainClass, File out, String target, List<File> classPaths) {
        this.roots = roots;
        this.out = out;
        this.target = target;
        this.classPaths = classPaths;
        this.mainClass = mainClass;
    }

    public static Args fromStrings(String... args) {
        return fromStrings(Arrays.asList(args));
    }

    public static Args fromStrings(List<String> args) {
        Mode mode = Mode.Default;
        String target = "js";
        List<File> root = new ArrayList<>();
        File out = null;
        List<File> classPaths = new ArrayList<>();
        Optional<String> mainClass = Optional.empty();
        for (String arg : args) {
            switch (mode) {
                case Default: {
                    switch (arg) {
                        case "-m": case "--main": {
                            mode = Mode.Main;
                            break;
                        }
                        case "-r": case "--root": {
                            mode = Mode.Root;
                            break;
                        }
                        case "-o": case "--out": {
                            mode = Mode.Out;
                            break;
                        }
                        case "-t": case "--target": {
                            mode = Mode.Target;
                            break;
                        }
                        case "-cp": case "--classpath": {
                            mode = Mode.ClassPath;
                            break;
                        }
                        default: {
                            throw new RuntimeException("Unrecognized command line argument: " + arg);
                        }
                    }
                    break;
                }
                case Main: {
                    mainClass = Optional.of(arg);
                    mode = Mode.Default;
                    break;
                }
                case Root: {
                    root.add(new File(arg));
                    mode = Mode.Default;
                    break;
                }
                case Out: {
                    out = new File(arg);
                    mode = Mode.Default;
                    break;
                }
                case Target: {
                    target = arg;
                    mode = Mode.Default;
                    break;
                }
                case ClassPath: {
                    classPaths.add(new File(arg));
                    mode = Mode.Default;
                    break;
                }
            }
        }
        if (root.isEmpty()) {
            throw new RuntimeException("At least one '-r/--root' directory must be specified");
        }
        if (out == null) {
            throw new RuntimeException("A '-o/--out' directory must be specified");
        }
        return new Args(root, mainClass, out, target, classPaths);
    }

    private enum Mode {
        Default, Main, Root, Out, Target, ClassPath,
    }
}
