package crossj;

public final class Repr {
    private Repr() {}

    public static String of(Object x) {
        if (x instanceof String) {
            return reprstr((String) x);
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
}