package com.github.math4tots.crossj.ast;

import crossj.Map;

public interface Type {
    public Type applyBinding(Map<String, Type> binding);
}
