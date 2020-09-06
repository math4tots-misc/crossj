package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

import crossj.XError;

public interface Node {
    Mark getMark();
    Node getParent();

    default World getWorld() {
        return getParent().getWorld();
    }

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

    default XError err(String message) {
        return XError.withMessage(message);
    }
}
