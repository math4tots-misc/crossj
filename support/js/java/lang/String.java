package java.lang;

/**
 * String<br/>
 *
 * behavior may be different for different targets depending on the target
 * platforms string representation.<br/>
 *
 * For UTF-16 (e.g. Java, ObjC, JavaScript), length() and charAt() should work
 * on 16-bit units (i.e. the default behavior on java)
 *
 * For UTF-8 representations, length() and charAt() should work on 8-bit units.
 */
public final class String implements Comparable<String> {
    // We don't want to allow String instances to be instantiated
    // this way
    private String() {
    }

    public native int length();

    public native char charAt(int i);

    public native String substring(int beginIndex, int endIndex);

    public native boolean startsWith(String prefix);

    public native boolean endsWith(String suffix);

    @Override
    public native int hashCode();

    @Override
    public native int compareTo(String o);

    public native String toLowerCase();

    public native String toUpperCase();
}
