package sanity;

import crossj.Assert;
import crossj.Test;

public final class ClassTest {
    public static int staticField = 123;
    public static int uninitializedStaticField;
    public static ClassTest uninitObjectStaticField;
    public int instanceField = 994;
    public int uninitializedInstanceField;
    public ClassTest uninitObjectInstanceField;

    private double foo(double x) {
        return x + instanceField;
    }

    private static String staticFoo(String x) {
        return x + x;
    }

    @Test
    public static void testFields() {
        Assert.equals(ClassTest.staticField, 123);
        Assert.equals(ClassTest.uninitializedStaticField, 0);
        Assert.equals(ClassTest.uninitObjectStaticField, null);
        Assert.equals(new ClassTest().instanceField, 994);
        Assert.equals(new ClassTest().uninitializedInstanceField, 0);
        Assert.equals(new ClassTest().uninitObjectInstanceField, null);

        Assert.equals(ClassTest.staticField = 77, 77);
        Assert.equals(ClassTest.staticField, 77);

        ClassTest x = new ClassTest();
        Assert.equals(x.instanceField = 49, 49);
        Assert.equals(x.instanceField, 49);
    }

    @Test
    public static void testMethods() {
        ClassTest x = new ClassTest();
        Assert.equals(x.foo(6), 1000.0);
        Assert.equals(ClassTest.staticFoo("hi"), "hihi");
    }
}
