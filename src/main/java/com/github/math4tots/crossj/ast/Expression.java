package com.github.math4tots.crossj.ast;

public interface Expression extends Node {
    void setParent(Node parent);

    <R, A> R accept(ExpressionVisitor<R, A> visitor, A a);
}
