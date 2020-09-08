package com.github.math4tots.crossj.parser;

public final class Mark {
    public final Source source;
    public final int line;
    public final int column;

    public Mark(Source source, int line, int column) {
        this.source = source;
        this.line = line;
        this.column = column;
    }

    public String format() {
        return "in " + source.name + " on line " + line + " on column " + column + "\n";
    }
}
