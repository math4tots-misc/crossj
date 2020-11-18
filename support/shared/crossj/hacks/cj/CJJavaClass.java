package crossj.hacks.cj;

/**
 * A CJ class translated to java.
 */
public final class CJJavaClass {
    private final String packageName;
    private final String shortClassName;
    private final String source;

    CJJavaClass(String packageName, String shortClassName, String source) {
        this.packageName = packageName;
        this.shortClassName = shortClassName;
        this.source = source;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getShortClassName() {
        return shortClassName;
    }

    public String getQualifiedClassName() {
        return packageName + "." + shortClassName;
    }

    public String getSource() {
        return source;
    }
}
