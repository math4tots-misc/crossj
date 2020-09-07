package com.github.math4tots.crossj.ast;

/**
 * The type for package name parts of expressions
 *
 * e.g. in
 *      java.lang.String.join(...)
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
}
