package com.github.math4tots.crossj;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import crossj.XError;

import org.eclipse.jdt.core.dom.*;

public abstract class DefaultVisitor extends ASTVisitor {
    public boolean defaultVisit(ASTNode node) {
        throw unexpected(node);
    }

    public XError unexpected(ASTNode node) {
        throw XError.withMessage("Unexpected node: " + node.getClass());
    }

    public boolean visit(AnnotationTypeDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(AnnotationTypeMemberDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(AnonymousClassDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(ArrayAccess node) {
        return defaultVisit(node);
    }

    public boolean visit(ArrayCreation node) {
        return defaultVisit(node);
    }

    public boolean visit(ArrayInitializer node) {
        return defaultVisit(node);
    }

    public boolean visit(ArrayType node) {
        return defaultVisit(node);
    }

    public boolean visit(AssertStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(Assignment node) {
        return defaultVisit(node);
    }

    public boolean visit(Block node) {
        return defaultVisit(node);
    }

    public boolean visit(BlockComment node) {
        return defaultVisit(node);
    }

    public boolean visit(BooleanLiteral node) {
        return defaultVisit(node);
    }

    public boolean visit(BreakStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(CastExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(CatchClause node) {
        return defaultVisit(node);
    }

    public boolean visit(CharacterLiteral node) {
        return defaultVisit(node);
    }

    public boolean visit(ClassInstanceCreation node) {
        return defaultVisit(node);
    }

    public boolean visit(CompilationUnit node) {
        return defaultVisit(node);
    }

    public boolean visit(ConditionalExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(ConstructorInvocation node) {
        return defaultVisit(node);
    }

    public boolean visit(ContinueStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(CreationReference node) {
        return defaultVisit(node);
    }

    public boolean visit(Dimension node) {
        return defaultVisit(node);
    }

    public boolean visit(DoStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(EmptyStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(EnhancedForStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(EnumConstantDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(EnumDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(ExportsDirective node) {
        return defaultVisit(node);
    }

    public boolean visit(ExpressionMethodReference node) {
        return defaultVisit(node);
    }

    public boolean visit(ExpressionStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(FieldAccess node) {
        return defaultVisit(node);
    }

    public boolean visit(FieldDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(ForStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(IfStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(ImportDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(InfixExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(Initializer node) {
        return defaultVisit(node);
    }

    public boolean visit(InstanceofExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(IntersectionType node) {
        return defaultVisit(node);
    }

    public boolean visit(Javadoc node) {
        return defaultVisit(node);
    }

    public boolean visit(LabeledStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(LambdaExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(LineComment node) {
        return defaultVisit(node);
    }

    public boolean visit(MarkerAnnotation node) {
        return defaultVisit(node);
    }

    public boolean visit(MemberRef node) {
        return defaultVisit(node);
    }

    public boolean visit(MemberValuePair node) {
        return defaultVisit(node);
    }

    public boolean visit(MethodRef node) {
        return defaultVisit(node);
    }

    public boolean visit(MethodRefParameter node) {
        return defaultVisit(node);
    }

    public boolean visit(MethodDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(MethodInvocation node) {
        return defaultVisit(node);
    }

    public boolean visit(Modifier node) {
        return defaultVisit(node);
    }

    public boolean visit(ModuleDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(ModuleModifier node) {
        return defaultVisit(node);
    }

    public boolean visit(NameQualifiedType node) {
        return defaultVisit(node);
    }

    public boolean visit(NormalAnnotation node) {
        return defaultVisit(node);
    }

    public boolean visit(NullLiteral node) {
        return defaultVisit(node);
    }

    public boolean visit(NumberLiteral node) {
        return defaultVisit(node);
    }

    public boolean visit(OpensDirective node) {
        return defaultVisit(node);
    }

    public boolean visit(PackageDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(ParameterizedType node) {
        return defaultVisit(node);
    }

    public boolean visit(ParenthesizedExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(PostfixExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(PrefixExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(ProvidesDirective node) {
        return defaultVisit(node);
    }

    public boolean visit(PrimitiveType node) {
        return defaultVisit(node);
    }

    public boolean visit(QualifiedName node) {
        return defaultVisit(node);
    }

    public boolean visit(QualifiedType node) {
        return defaultVisit(node);
    }

    public boolean visit(RequiresDirective node) {
        return defaultVisit(node);
    }

    public boolean visit(RecordDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(ReturnStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(SimpleName node) {
        return defaultVisit(node);
    }

    public boolean visit(SimpleType node) {
        return defaultVisit(node);
    }

    public boolean visit(SingleMemberAnnotation node) {
        return defaultVisit(node);
    }

    public boolean visit(SingleVariableDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(StringLiteral node) {
        return defaultVisit(node);
    }

    public boolean visit(SuperConstructorInvocation node) {
        return defaultVisit(node);
    }

    public boolean visit(SuperFieldAccess node) {
        return defaultVisit(node);
    }

    public boolean visit(SuperMethodInvocation node) {
        return defaultVisit(node);
    }

    public boolean visit(SuperMethodReference node) {
        return defaultVisit(node);
    }

    public boolean visit(SwitchCase node) {
        return defaultVisit(node);
    }

    public boolean visit(SwitchExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(SwitchStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(SynchronizedStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(TagElement node) {
        return defaultVisit(node);
    }

    public boolean visit(TextBlock node) {
        return defaultVisit(node);
    }

    public boolean visit(TextElement node) {
        return defaultVisit(node);
    }

    public boolean visit(ThisExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(ThrowStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(TryStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(TypeDeclaration node) {
        return defaultVisit(node);
    }

    public boolean visit(TypeDeclarationStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(TypeLiteral node) {
        return defaultVisit(node);
    }

    public boolean visit(TypeMethodReference node) {
        return defaultVisit(node);
    }

    public boolean visit(TypeParameter node) {
        return defaultVisit(node);
    }

    public boolean visit(UnionType node) {
        return defaultVisit(node);
    }

    public boolean visit(UsesDirective node) {
        return defaultVisit(node);
    }

    public boolean visit(VariableDeclarationExpression node) {
        return defaultVisit(node);
    }

    public boolean visit(VariableDeclarationStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(VariableDeclarationFragment node) {
        return defaultVisit(node);
    }

    public boolean visit(WhileStatement node) {
        return defaultVisit(node);
    }

    public boolean visit(WildcardType node) {
        return defaultVisit(node);
    }

    public boolean visit(YieldStatement node) {
        return defaultVisit(node);
    }
}
