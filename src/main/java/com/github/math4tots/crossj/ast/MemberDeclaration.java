package com.github.math4tots.crossj.ast;

import crossj.List;

public interface MemberDeclaration extends Node {
    public List<String> getModifiers();
    public String getName();

    default boolean isStatic() {
        return getModifiers().contains("static");
    }
}
