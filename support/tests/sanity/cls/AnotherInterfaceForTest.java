package sanity.cls;

public interface AnotherInterfaceForTest {
    default String foo() {
        return "default foo() result";
    }
}
