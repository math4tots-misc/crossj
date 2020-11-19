package crossj.base;

public final class OSImpl {
    native static String getEnvOrNull(String key);
}
