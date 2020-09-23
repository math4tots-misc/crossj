package crossj.hacks.c;

public final class Source {
    private final String name;
    private final String data;

    private Source(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public static Source of(String name, String data) {
        return new Source(name, data);
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }
}
