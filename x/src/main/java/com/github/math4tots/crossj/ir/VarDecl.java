package com.github.math4tots.crossj.ir;

import crossj.Optional;
import crossj.Tuple;

public final class VarDecl implements Stmt {
    private final int lineno;
    private final Tuple<String> qualifiers;
    private final String reifiedType;
    private final String erasureType;
    private final String name;
    private final Optional<Expr> init;

    public VarDecl(int lineno, Tuple<String> qualifiers, String reifiedType, String erasureType, String name,
            Optional<Expr> init) {
        this.lineno = lineno;
        this.qualifiers = qualifiers;
        this.reifiedType = reifiedType;
        this.erasureType = erasureType;
        this.name = name;
        this.init = init;
    }

    @Override
    public int getLineno() {
        return lineno;
    }

    public Tuple<String> getQualifiers() {
        return qualifiers;
    }

    public boolean isStatic() {
        return qualifiers.iter().any(x -> x.equals("static"));
    }

    public String getReifiedType() {
        return reifiedType;
    }

    public String getErasureType() {
        return erasureType;
    }

    public String getName() {
        return name;
    }

    public Optional<Expr> getInit() {
        return init;
    }
}
