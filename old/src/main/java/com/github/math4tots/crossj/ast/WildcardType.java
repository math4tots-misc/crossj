package com.github.math4tots.crossj.ast;

import crossj.Map;

public final class WildcardType implements Type {
    public static final WildcardType INSTANCE = new WildcardType();

    private WildcardType() {}

    @Override
    public Type applyBinding(Map<String, Type> binding) {
        return this;
    }
}
