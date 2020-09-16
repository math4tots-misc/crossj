package crossj;

/**
 * Some basic utils for manipulating strings.
 */
public final class Str {
    private Str() {}

    /**
     * Returns a UTF code unit.
     *
     * The actual value returned depends on the platform.
     *
     * There are three possible cases:
     *
     * <li>1: UTF-16.
     * Targets: Java, JavaScript, C#.
     * In this scenario, all strings are assumed to be represented internally as UTF-16.
     * The method will return a value between 0 and 65535 representing the UTF-16 code unit.
     * </li>
     * <li>2: UTF-8.
     * Targets: C/C++, Rust.
     * In this scenario, all strings are assumed to be represented internally as UTF-8.
     * The method will return a value between 0 and 255 representing a UTF-8 code unit.
     * </li>
     * <li>3: UCS-4/UTF-32.
     * Targets: Python.
     * Every unicode character can be indexed directly.
     * The method will return
     * </li>
     *
     */
    public static int codeAt(String string, int index) {
        return StrImpl.codeAt(string, index);
    }

    public static int code(char c) {
        return StrImpl.charCode(c);
    }

    public static String join(String separator, XIterable<?> iterable) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object obj : iterable) {
            if (!first) {
                sb.append(separator);
            }
            first = false;
            sb.append(obj);
        }
        return sb.toString();
    }

    public static List<String> split(String string, String separator) {
        List<String> parts = List.of();
        int len = string.length();
        int nextStart = 0, i = 0;
        while (i < len) {
            if (startsWithAt(string, separator, i)) {
                parts.add(string.substring(nextStart, i));
                i += separator.length();
                nextStart = i;
            } else {
                i++;
            }
        }
        parts.add(string.substring(nextStart, len));
        return parts;
    }

    public static boolean isSpaceChar(char ch) {
        return ImplChar.isWhitespace(ch);
    }

    public static List<String> lines(String string) {
        return split(string, "\n");
    }

    public static List<String> words(String string) {
        List<String> parts = List.of();
        int len = string.length();
        int nextStart = 0, i = 0;
        while (i < len) {
            if (isSpaceChar(string.charAt(i))) {
                if (i > nextStart) {
                    parts.add(string.substring(nextStart, i));
                }
                while (i < len && isSpaceChar(string.charAt(i))) {
                    i++;
                }
                nextStart = i;
            } else {
                i++;
            }
        }
        if (len > nextStart) {
            parts.add(string.substring(nextStart, len));
        }
        return parts;
    }

    public static boolean startsWithAt(String string, String prefix, int start) {
        return equalsSubstring(string, prefix, start, start + prefix.length());
    }

    public static boolean startsWith(String string, String prefix) {
        return startsWithAt(string, prefix, 0);
    }

    public static boolean endsWithAt(String string, String suffix, int end) {
        return equalsSubstring(string, suffix, end - suffix.length(), end);
    }

    public static boolean endsWith(String string, String suffix) {
        return endsWithAt(string, suffix, string.length());
    }

    public static boolean equalsSubstring(String string, String part, int start, int end) {
        if (start < 0) {
            start = 0;
        }
        if (end > string.length()) {
            end = string.length();
        }
        if (part.length() != end - start) {
            return false;
        }
        return string.substring(start, end).equals(part);
    }
}
