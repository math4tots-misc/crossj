package com.github.math4tots.crossj;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import crossj.List;
import crossj.XError;

public interface ITranslator {
    void setOutputDirectory(String path);

    void setMain(String main);

    void translate(String filepath, CompilationUnit compilationUnit);

    void commit();

    XError err(String message, ASTNode... nodes);

    public static String getQualifiedName(TypeDeclaration declaration) {
        return ((CompilationUnit) declaration.getRoot()).getPackage().getName().getFullyQualifiedName() + "."
                + declaration.getName().getFullyQualifiedName();
    }

    @SuppressWarnings("unchecked")
    public static List<IExtendedModifier> getExtendedModifiers(TypeDeclaration declaration) {
        return List.fromIterable(declaration.modifiers()).map(m -> (IExtendedModifier) m);
    }

    public static List<Modifier> getModifiers(TypeDeclaration declaration) {
        return getExtendedModifiers(declaration).filter(m -> (m instanceof Modifier))
                .map(m -> (Modifier) m);
    }

    public static boolean isStatic(TypeDeclaration declaration) {
        return getModifiers(declaration).iter().any(m -> m.isStatic());
    }

    public static boolean isStatic(MethodDeclaration declaration) {
        return getModifiers(declaration).iter().any(m -> m.isStatic());
    }

    public static boolean isFinal(TypeDeclaration declaration) {
        return getModifiers(declaration).iter().any(m -> m.isFinal());
    }

    @SuppressWarnings("unchecked")
    public static List<SingleVariableDeclaration> getParameters(MethodDeclaration declaration) {
        return List.fromIterable(declaration.parameters()).map(d -> (SingleVariableDeclaration) d);
    }

    @SuppressWarnings("unchecked")
    public static List<IExtendedModifier> getExtendedModifiers(MethodDeclaration method) {
        return List.fromIterable(method.modifiers()).map(m -> (IExtendedModifier) m);
    }

    public static List<Modifier> getModifiers(MethodDeclaration method) {
        return getExtendedModifiers(method).filter(m -> (m instanceof Modifier)).map(m -> (Modifier) m);
    }

    public static boolean isNative(MethodDeclaration method) {
        return getModifiers(method).iter().any(m -> m.isNative());
    }

    default public boolean isNative(TypeDeclaration declaration) {
        // if there's at least 1 native method, we consider it native
        // the other methods are assumed to be placeholders.
        for (MethodDeclaration method : declaration.getMethods()) {
            if (!method.isConstructor()) {
                if (isNative(method)) {
                    return true;
                }
            }
        }
        return false;
    }
}
