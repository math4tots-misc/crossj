package sanity.cls;

import crossj.Assert;
import crossj.Func1;
import crossj.Func2;
import crossj.List;
import crossj.Pair;
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

    @Test
    public static void instanceOf() {
        ClassForTest x = new ClassForTest();
        Assert.that(x instanceof ClassForTest);
        Assert.that(x instanceof InterfaceForTest);
        Assert.that(x instanceof AnotherInterfaceForTest);
        Assert.that(x instanceof Object);
        InterfaceForTest y = x;
        Assert.that(y instanceof ClassForTest);
        Assert.that(y instanceof InterfaceForTest);
        Assert.that(y instanceof AnotherInterfaceForTest);
        Object z = y;
        Assert.that(z instanceof ClassForTest);
        Assert.that(z instanceof InterfaceForTest);
        Assert.that(z instanceof AnotherInterfaceForTest);
        Assert.that(!(z instanceof Pair<?, ?>));
        Assert.that(!(z instanceof ClassTest));
    }

    @Test
    public static void instanceOfGeneric() {
        Object x = List.of(1, 2, 3);
        Assert.that(x instanceof List<?>);
    }

    @Test
    public static void instanceOfFunc() {
        Func2<Integer, Integer, Integer> f = (a, b) -> a + b;
        Assert.that(f instanceof Func2<?, ?, ?>);
        Assert.that(f instanceof Object);
        Object g = f;
        Assert.that(g instanceof Func2<?, ?, ?>);
        Assert.that(!(g instanceof Func1<?, ?>));
        Assert.that(g instanceof Object);
        Assert.that(!(g instanceof ClassForTest));
        Assert.that(!(g instanceof InterfaceForTest));
    }

    @Test
    public static void defaultMethods() {
        {
            ClassForTest x = new ClassForTest();
            Assert.equals(x.foo(), "default foo() result");
            AnotherClassForTest y = new AnotherClassForTest();
            Assert.equals(y.foo(), "overriden foo() return value");
        }
        {
            AnotherInterfaceForTest x = new ClassForTest();
            Assert.equals(x.foo(), "default foo() result");
            AnotherInterfaceForTest y = new AnotherClassForTest();
            Assert.equals(y.foo(), "overriden foo() return value");
        }
    }
}
