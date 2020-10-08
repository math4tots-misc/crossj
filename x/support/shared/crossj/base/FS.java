package crossj.base;

/**
 * File System operations
 */
public final class FS {
    private FS() {}

    private static final String SEPARATOR = FSImpl.getSeparator();

    public static String getSeparator() {
        return SEPARATOR;
    }

    public static String getWorkingDirectory() {
        return FSImpl.getWorkingDirectory();
    }

    public static String join(String... paths) {
        return FSImpl.joinPaths(List.fromJavaArray(paths));
    }
}
