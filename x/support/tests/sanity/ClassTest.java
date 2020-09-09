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

    @Test
    public static void testFields() {
        Assert.equals(ClassTest.staticField, 123);
        Assert.equals(ClassTest.uninitializedStaticField, 0);
        Assert.equals(ClassTest.uninitObjectStaticField, null);
        Assert.equals(new ClassTest().instanceField, 994);
        Assert.equals(new ClassTest().uninitializedInstanceField, 0);
        Assert.equals(new ClassTest().uninitObjectInstanceField, null);
    }
}
