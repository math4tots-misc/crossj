package com.github.math4tots.crossj;

import com.github.math4tots.crossj.ast.ClassExpressionType;
import com.github.math4tots.crossj.ast.ClassOrInterfaceDeclaration;
import com.github.math4tots.crossj.ast.DoubleLiteralExpression;
import com.github.math4tots.crossj.ast.Expression;
import com.github.math4tots.crossj.ast.ExpressionVisitor;
import com.github.math4tots.crossj.ast.FieldAccessExpression;
import com.github.math4tots.crossj.ast.InstanceOfExpression;
import com.github.math4tots.crossj.ast.IntegerLiteralExpression;
import com.github.math4tots.crossj.ast.MethodCallExpression;
import com.github.math4tots.crossj.ast.NameExpression;
import com.github.math4tots.crossj.ast.OperationExpression;
import com.github.math4tots.crossj.ast.PackageType;
import com.github.math4tots.crossj.ast.PrimitiveType;
import com.github.math4tots.crossj.ast.StringLiteralExpression;
import com.github.math4tots.crossj.ast.Type;
import com.github.math4tots.crossj.ast.TypeCastExpression;
import com.github.math4tots.crossj.ast.TypeExpression;
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
        }
        return null;
    }

    @Override
    public Type visitMethodCallExpression(MethodCallExpression n, Void a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type visitOperationExpression(OperationExpression n, Void a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type visitStringLiteralExpression(StringLiteralExpression n, Void a) {
        // TODO Auto-generated method stub
        return null;
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
