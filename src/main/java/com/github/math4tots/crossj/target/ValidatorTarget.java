package com.github.math4tots.crossj.target;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.ResolvedWildcard;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
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
        public void visit(FieldDeclaration n, Void arg) {
            for (VariableDeclarator decl : n.getVariables()) {
                decl.accept(this, arg);
                if (!n.isStatic() && decl.getInitializer().isPresent()) {
                    throw err("Initializers in non-static field declarations are not currently supported", decl);
                }
            }
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
                if (n.isAbstract()) {
                    throw err("Abstract methods are not allowed", n);
                }
                if (!n.getBody().isPresent() && !n.isNative()) {
                    throw err("Method is missing a body", n);
                }
            }
            n.getBody().ifPresent(body -> body.accept(this, arg));
        }

        @Override
        public void visit(BlockStmt n, Void arg) {
            n.getStatements().forEach(s -> s.accept(this, arg));
        }

        @Override
        public void visit(ReturnStmt n, Void arg) {
            n.getExpression().ifPresent(e -> e.accept(this, arg));
        }

        @Override
        public void visit(ExpressionStmt n, Void arg) {
            ResolvedType type = n.getExpression().calculateResolvedType();
            checkIfAllowedType(type, n);
            n.getExpression().accept(this, arg);
        }

        @Override
        public void visit(FieldAccessExpr n, Void arg) {
            n.getScope().accept(this, arg);
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            n.getScope().ifPresent(scope -> scope.accept(this, arg));
            n.getArguments().forEach(marg -> marg.accept(this, arg));
        }

        @Override
        public void visit(ObjectCreationExpr n, Void arg) {
            n.getArguments().forEach(marg -> marg.accept(this, arg));
        }

        @Override
        public void visit(EnclosedExpr n, Void arg) {
            n.getInner().accept(this, arg);
        }

        @Override
        public void visit(NameExpr n, Void arg) {
        }

        @Override
        public void visit(ThisExpr n, Void arg) {
        }

        @Override
        public void visit(StringLiteralExpr n, Void arg) {
        }

        @Override
        public void visit(CharLiteralExpr n, Void arg) {
        }

        @Override
        public void visit(BooleanLiteralExpr n, Void arg) {
        }

        @Override
        public void visit(IntegerLiteralExpr n, Void arg) {
            checkIfAllowedType(n.calculateResolvedType(), n);
        }

        @Override
        public void visit(LongLiteralExpr n, Void arg) {
            checkIfAllowedType(n.calculateResolvedType(), n);
        }

        @Override
        public void visit(DoubleLiteralExpr n, Void arg) {
            checkIfAllowedType(n.calculateResolvedType(), n);
        }

        @Override
        public void visit(UnaryExpr n, Void arg) {
            n.getExpression().accept(this, arg);
        }

        @Override
        public void visit(BinaryExpr n, Void arg) {
            n.getLeft().accept(this, arg);
            n.getRight().accept(this, arg);
        }

        @Override
        public void visit(AssignExpr n, Void arg) {
            n.getTarget().accept(this, arg);
            n.getValue().accept(this, arg);
        }

        @Override
        public void visit(VariableDeclarationExpr n, Void arg) {
            n.getVariables().forEach(v -> v.accept(this, arg));
        }

        @Override
        public void visit(VariableDeclarator n, Void arg) {
            checkIfAllowedType(getType(n), n);
        }

        @Override
        public void visit(CastExpr n, Void arg) {
            checkIfAllowedType(getType(n), n);
            n.getExpression().accept(this, arg);
        }

        @Override
        public void visit(IfStmt n, Void arg) {
            n.getCondition().accept(this, arg);
            n.getThenStmt().accept(this, arg);
            n.getElseStmt().ifPresent(s -> s.accept(this, arg));
        }

        @Override
        public void visit(ForStmt n, Void arg) {
            if (n.getInitialization().size() > 1) {
                throw err("for statements can only have at most one init part", n);
            }
            n.getInitialization().forEach(i -> i.accept(this, arg));
            n.getCompare().ifPresent(c -> c.accept(this, arg));
            n.getUpdate().forEach(upd -> upd.accept(this, arg));
        }

        @Override
        public void visit(InstanceOfExpr n, Void arg) {
            // instanceof checks are only allowed on:
            // 1. simple class types, or
            // 2. parameterized type with all '?' wildcards
            //
            // (2) allows us to effectively do instanceof checks
            // on generics without using raw types causing rawtype
            // warnings.
            //
            n.getExpression().accept(this, arg);
            ResolvedType rawType = getType(n);
            if (!rawType.isReferenceType()) {
                throw err("instanceof checks are only allowed on reference types", n);
            }
            ResolvedReferenceType type = rawType.asReferenceType();
            type.getTypeParametersMap().stream().map(pair -> pair.b).forEach(argtype -> {
                if (!argtype.equals(ResolvedWildcard.UNBOUNDED)) {
                    throw err("Only unbounded wildcards are allowed in instanceof reference types", n);
                }
            });
        }

        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            n.getBody().accept(this, arg);
        }
    }

    private ResolvedType getType(NodeWithType<?, ?> node) {
        JavaSymbolSolver solver = parser.getSymbolSolver();
        Type type = node.getType();
        return solver.toResolvedType(type, ResolvedType.class);
    }

    private void checkIfAllowedType(ResolvedType type, Node... nodes) {
        if (type.isPrimitive()) {
            switch (type.asPrimitive()) {
                case FLOAT:
                case LONG: {
                    throw err("Type " + type + " is not allowed in crossj", nodes);
                }
                default:
                    break;
            }
        } else if (type.isReferenceType()) {
            String qname = type.asReferenceType().getQualifiedName();
            switch (qname) {
                case "java.lang.Long": {
                    throw err("Type " + qname + " is not allowed in crossj", nodes);
                }
                default:
                    break;
            }
        }
    }
}
