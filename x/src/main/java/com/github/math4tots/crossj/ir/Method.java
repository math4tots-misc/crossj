package com.github.math4tots.crossj.ir;

import crossj.Optional;
import crossj.Tuple;

public final class Method {
    private final int lineno;
    private final Tuple<String> qualifiers;
    private final String reifiedReturnType;
    private final String erasureReturnType;
    private final String name;
    private final Tuple<VarDecl> parameters;
    private final boolean isVariadic;
    private final Optional<Block> body;

    public Method(int lineno, Tuple<String> qualifiers, String reifiedReturnType, String erasureReturnType, String name,
            Tuple<VarDecl> parameters, boolean isVariadic, Optional<Block> body) {
        this.lineno = lineno;
        this.qualifiers = qualifiers;
        this.reifiedReturnType = reifiedReturnType;
        this.erasureReturnType = erasureReturnType;
        this.name = name;
        this.parameters = parameters;
        this.isVariadic = isVariadic;
        this.body = body;
    }

    public int getLineno() {
        return lineno;
    }

    public Tuple<String> getQualifiers() {
        return qualifiers;
    }

    public String getReifiedReturnType() {
        return reifiedReturnType;
    }

    public String getErasureReturnType() {
        return erasureReturnType;
    }

    public String getName() {
        return name;
    }

    public Tuple<VarDecl> getParameters() {
        return parameters;
    }

    public boolean isVariadic() {
        return isVariadic;
    }

    public Optional<Block> getBody() {
        return body;
    }
}
