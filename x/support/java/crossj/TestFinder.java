package crossj;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class TestFinder {

    /**
     * Find and run all tests
     */
    public static void run(String packageName) {
        int testCount = 0;
        for (Method method : findAllTests(packageName)) {
            testCount++;
            System.out.print("Running test " + method.getDeclaringClass().getName() + "." + method.getName() + "... ");
            try {
                method.invoke(null, new Object[0]);
            } catch (Exception e) {
                IO.println("FAILED");
                throw new RuntimeException(e);
            }
            System.out.println("OK");
        }
        IO.println(testCount + " tests pass (Java)");
    }

    private static List<Method> findAllTests(String packageName) {
        List<Method> list = List.of();

        for (Class<?> cls : findAllClasses(packageName)) {
            for (Method method : cls.getMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation instanceof Test) {
                        list.add(method);
                    }
                }
            }
        }

        return list;
    }

    private static List<Class<?>> findAllClasses(String packageName) {
        // TODO: automate this...
        List<Class<?>> classes = List.of();
        try {
            List<Pair<Path, String>> basePaths = List.of(Pair.of(Paths.get("support", "tests", "sanity"), "sanity."));
            for (Pair<Path, String> pair : basePaths) {
                Path path = pair.get1();
                String prefix = pair.get2();
                List<String> baseNames = List.fromIterable(Files.list(path).collect(Collectors.toList()))
                        .map(p -> p.getFileName().toString())
                        .filter(name -> name.endsWith(".java"))
                        .map(name -> name.substring(0, name.length() - ".java".length()));
                List<String> classNames = baseNames.map(basename -> prefix + basename);
                for (String className : classNames) {
                    classes.add(Class.forName(className));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String packagePrefix = (packageName == null || packageName.length() == 0) ? "" : (packageName + ".");
        return classes.filter(cls -> cls.getCanonicalName().startsWith(packagePrefix));
    }

    // doesn't seem to work anymore with Java 14...
    // private static List<Class<?>> findAllClasses(String packageName) {
    // try {
    // List<Class<?>> ret = List.of();
    // ClassLoader cl = Thread.currentThread().getContextClassLoader();
    // Class<?> clclass = ClassLoader.class;
    // Field classesField = clclass.getDeclaredField("classes");
    // classesField.setAccessible(true);
    // java.util.List<?> classes = (java.util.List<?>) classesField.get(cl);
    // for (Object obj : classes) {
    // Class<?> cls = (Class<?>) obj;
    // if (packageName != null) {
    // if (!cls.getName().startsWith(packageName + ".")) {
    // continue;
    // }
    // }
    // ret.add(cls);
    // }
    // return ret;
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }
}