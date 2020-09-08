package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.TypeSolver;
import com.github.math4tots.crossj.parser.Mark;

import crossj.XError;

public interface Node {
    Mark getMark();

    Node getParent();

    default World getWorld() {
        return getParent().getWorld();
    }

    default ClassOrInterfaceDeclaration getDeclaringClassOrInterfaceDeclaration() {
        return getParent().getDeclaringClassOrInterfaceDeclaration();
    }

    default TypeSolver getTypeSolver() {
        return getParent().getTypeSolver();
    }

    /**
     * Looks up a type declaration in the context of the given node. returns null if
     * none is found.
     *
     * @param name
     * @return
     */
    default TypeDeclaration lookupTypeDeclaration(String name) {
        return getParent().lookupTypeDeclaration(name);
    }

    default TypeDeclaration lookupTypeDeclarationOrThrow(String name) {
        TypeDeclaration declaration = lookupTypeDeclaration(name);
        if (declaration == null) {
            throw err("Type " + name + "not found");
        }
        return declaration;
    }

    /**
     * Looks up a variable or field declaration given the name, in the given
     * context. returns null if none is found.
     *
     * @param name
     * @return
     */
    default VariableDeclaration lookupVariableDeclaration(String name) {
        return getParent().lookupVariableDeclaration(name);
    }

    default VariableDeclaration lookupVariableDeclarationOrThrow(String name) {
        VariableDeclaration declaration = lookupVariableDeclaration(name);
        if (declaration == null) {
            throw err("Variable " + name + " not found");
        }
        return declaration;
    }

    /**
     * Looks up a method declaration given the name, in the given context. Returns
     * null if none is found.
     *
     * @param name
     * @return
     */
    default MethodDeclaration lookupMethodDeclaration(String name) {
        return getParent().lookupMethodDeclaration(name);
    }

    default MethodDeclaration lookupMethodDeclarationOrThrow(String name) {
        MethodDeclaration declaration = lookupMethodDeclaration(name);
        if (declaration == null) {
            throw err("Method " + name + " not found");
        }
        return declaration;
    }

    default XError err(String message) {
        return XError.withMessage(message);
    }
}
