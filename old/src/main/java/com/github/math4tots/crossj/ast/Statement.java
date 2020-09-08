package com.github.math4tots.crossj.ast;

public interface Statement extends Node {
    void setParent(Node parent);
    <R, A> R accept(StatementVisitor<R, A> visitor, A a);
}
