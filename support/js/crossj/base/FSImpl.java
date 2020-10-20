package crossj.base;

public final class FSImpl {
    private FSImpl() {}
    native static String getSeparator();
    native static String getWorkingDirectory();
    native static String joinPaths(XIterable<String> paths);
}
