package cjx.compiler

import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJAstAssignmentTarget
import cjx.compiler.CJAstExpression
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstStatement: CJAstNode {
    case Expression(CJMark, CJAstExpression)
    case Return(CJMark, CJAstExpression)
    case Block(CJMark, List[CJAstStatement])
    case If(CJMark, CJAstExpression, CJAstStatement, Nullable[CJAstStatement])
    case While(CJMark, CJAstExpression, CJAstStatement)
    case For(CJMark, CJAstAssignmentTarget, CJAstExpression, CJAstStatement)
    case Union(
        CJMark,
        CJAstExpression,
        List[Tuple[String, List[String], CJAstStatement]],
        Nullable[CJAstStatement])
    case Switch(
        CJMark,
        CJAstExpression,
        List[Tuple[List[CJAstExpression], CJAstStatement]],
        Nullable[CJAstStatement])
    case VariableDeclaration(
        CJMark, Bool, CJAstAssignmentTarget, Nullable[CJAstTypeExpression], CJAstExpression)
    case Assignment(CJMark, CJAstAssignmentTarget, CJAstExpression)
    case AugmentedAssignment(CJMark, CJAstAssignmentTarget, String, CJAstExpression)

    def mark(self: Self): CJMark {
        return union self {
            case Expression(mark, expr) = mark
            case Return(mark, expr) = mark
            case Block(mark, stmts) = mark
            case If(mark, cond, body, other) = mark
            case While(mark, cond, body) = mark
            case For(mark, target, container, body) = mark
            case Union(mark, target, cases, alt) = mark
            case Switch(mark, target, cases, alt) = mark
            case VariableDeclaration(mark, mutable, target, type, expr) = mark
            case Assignment(mark, target, expr) = mark
            case AugmentedAssignment(mark, target, kind, expr) = mark
        }
    }

    def kind(self: Self): String {
        return union self {
            case Expression(mark, expr) = "Expression"
            case Return(mark, expr) = "Return"
            case Block(mark, stmts) = "Block"
            case If(mark, cond, body, other) = "If"
            case While(mark, cond, body) = "While"
            case For(mark, target, container, body) = "For"
            case Union(mark, target, cases, alt) = "Union"
            case Switch(mark, target, cases, alt) = "Switch"
            case VariableDeclaration(mark, mutable, target, type, expr) = "VariableDeclaration"
            case Assignment(mark, target, expr) = "Assignment"
            case AugmentedAssignment(mark, target, kind, expr) = "AugmentedAssignment"
        }
    }
}
