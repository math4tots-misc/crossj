package com.github.math4tots.crossj.ast;

import crossj.List;
import crossj.Map;

public final class Scope {
    private final Scope parent;
    private final Map<String, TypeDeclaration> globalTypeMap;
    private final Map<TypeExpression, Type> typeCache;
    private final Map<String, TypeDeclaration> typeMap = Map.of();

    public Scope(Scope parent) {
        this.parent = parent;
        if (parent == null) {
            globalTypeMap = Map.of();
            typeCache = Map.of();
        } else {
            globalTypeMap = parent.globalTypeMap;
            typeCache = parent.typeCache;
        }
    }

    public Scope getParent() {
        return parent;
    }

    public TypeDeclaration findTypeDeclaration(String key) {
        if (key.contains(".")) {
            // fully qualified name
            return globalTypeMap.get(key);
        } else {
            // unqualified name
            TypeDeclaration declaration = typeMap.get(key);
            if (declaration == null && parent != null) {
                return parent.findTypeDeclaration(key);
            } else {
                return declaration;
            }
        }
    }

    public Type solveType(TypeExpression typeExpression) {
        if (!typeCache.containsKey(typeExpression)) {
            typeCache.put(typeExpression, solveTypeNoCache(typeExpression));
        }
        return typeCache.get(typeExpression);
    }

    private Type solveTypeNoCache(TypeExpression typeExpression) {
        String name = typeExpression.getName();
        switch (name) {
            case "?": return WildcardType.INSTANCE;
            case "void": return PrimitiveType.VOID;
            case "char": return PrimitiveType.CHAR;
            case "int": return PrimitiveType.INT;
            case "double": return PrimitiveType.DOUBLE;
        }
        TypeDeclaration declaration = findTypeDeclaration(name);
        List<Type> arguments = null;
        if (typeExpression.hasArguments()) {
            arguments = List.of();
            for (TypeExpression arg: typeExpression.getArguments()) {
                arguments.add(solveType(arg));
            }
        }
        return new ReferenceType(declaration, arguments);
    }
}
