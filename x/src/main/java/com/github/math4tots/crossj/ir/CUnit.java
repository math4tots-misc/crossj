package com.github.math4tots.crossj.ir;

import crossj.Tuple;

/**
 * Compilation Unit
 *
 * In crossj, each compilation unit always contains exactly 1 class.
 */
public final class CUnit {
    private final String filepath;
    private final String packageName;
    private final Tuple<String> qualifiers;
    private final boolean isInterface;
    private final String shortClassName;
    private final Tuple<String> reifiedInterfaces;
    private final Tuple<String> erasureInterfaces;
    private final Tuple<VarDecl> fields;
    private final Tuple<Method> methods;

    public CUnit(String filepath, String packageName, Tuple<String> qualifiers, boolean isInterface,
            String shortClassName, Tuple<String> reifiedInterfaces, Tuple<String> erasureInterfaces,
            Tuple<VarDecl> fields, Tuple<Method> methods) {
        this.filepath = filepath;
        this.packageName = packageName;
        this.qualifiers = qualifiers;
        this.isInterface = isInterface;
        this.shortClassName = shortClassName;
        this.reifiedInterfaces = reifiedInterfaces;
        this.erasureInterfaces = erasureInterfaces;
        this.fields = fields;
        this.methods = methods;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getPackageName() {
        return packageName;
    }

    public Tuple<String> getQualifiers() {
        return qualifiers;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public String getQualifiedClassName() {
        return packageName + "." + shortClassName;
    }

    public String getShortClassName() {
        return shortClassName;
    }

    public Tuple<String> getReifiedInterfaces() {
        return reifiedInterfaces;
    }

    public Tuple<String> getErasureInterfaces() {
        return erasureInterfaces;
    }

    public Tuple<VarDecl> getFields() {
        return fields;
    }

    public Tuple<Method> getMethods() {
        return methods;
    }
}
