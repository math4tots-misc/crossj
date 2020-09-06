package com.github.math4tots.crossj.parser;

public final class Token {
    private final Mark mark;
    private final String type;
    private final Object value;

    public Token(Mark mark, String type, Object value) {
        this.mark = mark;
        this.type = type;
        this.value = value;
    }

    public Mark getMark() {
        return mark;
    }

    public String getType() {
        return type;
    }

    public int getInt() {
        return (int) value;
    }

    public double getDouble() {
        return (double) value;
    }

    public String getString() {
        return (String) value;
    }

    @Override
    public String toString() {
        return "Token(" + type + ", " + value + ")";
    }
}
