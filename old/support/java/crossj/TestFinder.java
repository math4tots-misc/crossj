package crossj;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
        IO.println(testCount + " TESTS PASS");
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
        try {
            List<Class<?>> ret = List.of();
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> clclass = ClassLoader.class;
            Field classesField = clclass.getDeclaredField("classes");
            classesField.setAccessible(true);
            java.util.List<?> classes = (java.util.List<?>) classesField.get(cl);
            for (Object obj : classes) {
                Class<?> cls = (Class<?>) obj;
                if (packageName != null) {
                    if (!cls.getName().startsWith(packageName + ".")) {
                        continue;
                    }
                }
                ret.add(cls);
            }
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
