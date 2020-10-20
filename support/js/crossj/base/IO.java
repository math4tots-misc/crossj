package crossj.base;

public final class IO {
    private IO() {}
    public static native void println(Object object);
    public static native void eprintln(Object object);
    public static native void print(Object object);
    public static native void eprint(Object object);
    public static native String join(String... parts);

    /** basically '\\' on windows, '/' everywhere else */
    public static native String separator();

    /** basically ':' on windows, ';' everywhere else */
    public static native String pathSeparator();

    // these io operations that actually read or write to the filesystem
    // require that you're on node.js and not on e.g. a browser

    public static native void writeFile(String path, String data);
    public static native void writeFileBytes(String path, Bytes data);
    public static native String readFile(String path);
    public static native Bytes readFileBytes(String path);
    public static native String readStdin();
    public static native Bytes readStdinBytes();
}
