package sanity;

public interface AnotherInterfaceForTest {
    default String foo() {
        return "default foo() result";
    }
}
