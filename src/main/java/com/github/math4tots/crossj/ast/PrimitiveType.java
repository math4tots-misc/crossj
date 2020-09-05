package com.github.math4tots.crossj.ast;

public final class PrimitiveType implements Type {

    public static final PrimitiveType VOID = new PrimitiveType("void");
    public static final PrimitiveType CHAR = new PrimitiveType("char");
    public static final PrimitiveType INT = new PrimitiveType("int");
    public static final PrimitiveType DOUBLE = new PrimitiveType("double");

    private final String name;

    private PrimitiveType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
