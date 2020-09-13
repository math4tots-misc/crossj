package crossj;

public interface Repr {
    String repr();

    public static String of(Object x) {
        if (x instanceof String) {
            return reprstr((String) x);
        } else if (x instanceof Repr) {
            return ((Repr) x).repr();
        } else {
            return x.toString();
        }
    }

    public static String reprstr(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\t': sb.append("\\t"); break;
                case '\r': sb.append("\\r"); break;
                case '\n': sb.append("\\n"); break;
                case '\0': sb.append("\\0"); break;
                case '\\': sb.append("\\\\"); break;
                case '\"': sb.append("\\\""); break;
                case '\'': sb.append("\\\'"); break;
                default: sb.append(c);
            }
        }
        sb.append('"');
        return sb.toString();
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
}
