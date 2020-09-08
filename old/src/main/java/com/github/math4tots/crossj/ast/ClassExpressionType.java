package com.github.math4tots.crossj.ast;

import crossj.Map;
import crossj.XError;

/**
 * The type of the class part of an expression.
 *
 * E.g. in
 *
 *      java.lang.String.join(..)
 *
 * the 'java.lang.String' part is the ClassExpressionType
 */
public final class ClassExpressionType implements PseudoType {
    private final ClassOrInterfaceDeclaration declaration;

    public ClassExpressionType(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    @Override
    public Type applyBinding(Map<String, Type> binding) {
        throw XError.withMessage("Cannot apply binding to ClassExpressionType");
    }
}
