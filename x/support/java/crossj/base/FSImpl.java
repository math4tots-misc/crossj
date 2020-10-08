package crossj.base;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used by FS
 */
public final class FSImpl {
    private FSImpl() {}

    static String getSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    static String joinPaths(XIterable<String> paths) {
        Path joined = null;
        for (var path: paths) {
            if (joined == null) {
                joined = Paths.get(path);
            } else {
                joined = joined.resolve(path);
            }
        }
        return joined.toString();
    }
}
