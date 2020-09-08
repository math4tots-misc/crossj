package com.github.math4tots.crossj;

import com.github.math4tots.crossj.ast.ClassExpressionType;
import com.github.math4tots.crossj.ast.ClassOrInterfaceDeclaration;
import com.github.math4tots.crossj.ast.DoubleLiteralExpression;
import com.github.math4tots.crossj.ast.Expression;
import com.github.math4tots.crossj.ast.ExpressionVisitor;
import com.github.math4tots.crossj.ast.FieldAccessExpression;
import com.github.math4tots.crossj.ast.FieldDeclaration;
import com.github.math4tots.crossj.ast.InstanceOfExpression;
import com.github.math4tots.crossj.ast.IntegerLiteralExpression;
import com.github.math4tots.crossj.ast.MethodCallExpression;
import com.github.math4tots.crossj.ast.MethodDeclaration;
import com.github.math4tots.crossj.ast.NameExpression;
import com.github.math4tots.crossj.ast.OperationExpression;
import com.github.math4tots.crossj.ast.PackageType;
import com.github.math4tots.crossj.ast.PrimitiveType;
import com.github.math4tots.crossj.ast.ReferenceType;
import com.github.math4tots.crossj.ast.StringLiteralExpression;
import com.github.math4tots.crossj.ast.Type;
import com.github.math4tots.crossj.ast.TypeCastExpression;
import com.github.math4tots.crossj.ast.World;

import crossj.Map;

public final class TypeSolver implements ExpressionVisitor<Type, Void> {
    private final World world;
    private final Map<Expression, Type> cache = Map.of();

    public TypeSolver(World world) {
        this.world = world;
    }

    public Type solveExpression(Expression e) {
        if (!cache.containsKey(e)) {
            cache.put(e, e.accept(this, null));
        }
        return cache.get(e);
    }

    @Override
    public Type visitFieldAccessExpression(FieldAccessExpression n, Void a) {
        Type scopeType = solveExpression(n.getScope());
        String name = n.getName();
        if (scopeType instanceof PackageType) {
            String qualifiedName = ((PackageType) scopeType).getName() + "." + name;
            ClassOrInterfaceDeclaration cls = world.lookupClassOrInterfaceDeclaration(qualifiedName);
            if (cls != null) {
                return new ClassExpressionType(cls);
            } else if (world.hasPackageWithName(qualifiedName)) {
                // just assume that this is part of a package name
                return new PackageType(qualifiedName);
            }
        } else if (scopeType instanceof ClassExpressionType) {
            ClassOrInterfaceDeclaration cls = ((ClassExpressionType) scopeType).getDeclaration();
            FieldDeclaration declaration = cls.lookupFieldDeclarationOrThrow(name);
            return declaration.getType().solveType();
        } else if (scopeType instanceof ReferenceType) {
            ReferenceType type = (ReferenceType) scopeType;
            Map<String, Type> binding = type.getTypeBinding();
            ClassOrInterfaceDeclaration cls = type.getDeclaration();
            FieldDeclaration declaration = cls.lookupFieldDeclarationOrThrow(name);
            return declaration.getType().solveType().applyBinding(binding);
        }
        throw n.err("Cannot access field of " + scopeType);
    }

    @Override
    public Type visitMethodCallExpression(MethodCallExpression n, Void a) {
        Type scopeType = solveExpression(n.getScope());
        String name = n.getName();
        if ((scopeType == null
                && n.getDeclaringClassOrInterfaceDeclaration().lookupMethodDeclarationOrThrow(name).isStatic())
                || scopeType instanceof ClassExpressionType) {
            // static method call
            // in this case we care only about the type parameters on the method itself.
            ClassOrInterfaceDeclaration cls = scopeType == null ? n.getDeclaringClassOrInterfaceDeclaration()
                    : ((ClassExpressionType) scopeType).getDeclaration();
            MethodDeclaration method = cls.lookupMethodDeclaration(name);
            if (!method.hasTypeParameters()) {
                return method.getReturnType().solveType();
            }
        }
        throw n.err("this kind of method call is not yet supported (scope class = " + scopeType.getClass() + ")");
    }

    @Override
    public Type visitOperationExpression(OperationExpression n, Void a) {
        throw n.err("Operations not yet supported");
    }

    @Override
    public Type visitStringLiteralExpression(StringLiteralExpression n, Void a) {
        return new ReferenceType(n, world.lookupClassOrInterfaceDeclaration("java.lang.String"), null);
    }

    @Override
    public Type visitIntegerLiteralExpression(IntegerLiteralExpression n, Void a) {
        return PrimitiveType.INT;
    }

    @Override
    public Type visitDoubleLiteralExpression(DoubleLiteralExpression n, Void a) {
        return PrimitiveType.DOUBLE;
    }

    @Override
    public Type visitNameExpression(NameExpression n, Void a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type visitTypeCastExpression(TypeCastExpression n, Void a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type visitInstanceOfExpression(InstanceOfExpression n, Void a) {
        // TODO Auto-generated method stub
        return null;
    }
}
