package crossj;

public final class IO {
    private IO() {}
    public static void println(Object object) {
        System.out.println(object.toString());
    }
    public static void eprintln(Object object) {
        System.err.println(object.toString());
    }
}
