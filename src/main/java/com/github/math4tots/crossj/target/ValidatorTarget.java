package com.github.math4tots.crossj.target;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.math4tots.crossj.Parser;

public class ValidatorTarget extends Target {
    public ValidatorTarget(Parser parser) {
        super(parser);
    }

    @Override
    public void emit(List<CompilationUnit> compilationUnits, Optional<String> mainClass, File out) {
        for (CompilationUnit compilationUnit : compilationUnits) {
            new Validator().visit(compilationUnit, null);
        }
    }

    private class Validator extends VoidVisitorWithDefaults<Void> {

        @Override
        public void defaultAction(Node n, Void arg) {
            throw err("Unexpected Node type: " + n.getClass(), n);
        }

        @Override
        public void visit(CompilationUnit n, Void arg) {
            if (n.getTypes().size() > 1) {
                throw err("CompilationUnits must contain only the primary type", n);
            }
            Optional<TypeDeclaration<?>> decl = n.getPrimaryType();
            if (decl.isPresent()) {
                decl.get().accept(this, null);
            } else {
                throw err("Primary type declaration is missing", n);
            }
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (!n.isInterface() && !n.isFinal()) {
                throw err("All classes must be final", n);
            }
            List<ConstructorDeclaration> constructors = n.getConstructors();
            if (constructors.size() > 1) {
                throw err("There can be at most one constructor but found " + constructors.size(), n);
            }
            n.getMembers().forEach(mem -> mem.accept(this, arg));
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (n.isStatic() && !n.isNative()) {
                if (!n.getBody().isPresent()) {
                    throw err("Non-native static methods must always have a method body", n);
                }
            }
            ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) n.getParentNode().get();
            if (!cls.isInterface()) {
                if (n.isAbstract()){
                    throw err("Abstract methods are not allowed", n);
                }
                if (!n.getBody().isPresent() && !n.isNative()) {
                    throw err("Method is missing a body", n);
                }
            }
        }

        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
        }
    }
}
