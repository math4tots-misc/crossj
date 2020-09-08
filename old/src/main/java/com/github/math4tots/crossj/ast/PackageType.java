package com.github.math4tots.crossj.ast;

import crossj.Map;
import crossj.XError;

/**
 * The type for package name parts of expressions
 *
 * e.g. in java.lang.String.join(...)
 *
 * the 'java.lang' part has a PackageType.
 */
public final class PackageType implements PseudoType {
    private final String name;

    public PackageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Type applyBinding(Map<String, Type> binding) {
        throw XError.withMessage("Cannot apply binding to package type (" + name + ")");
    }
}
