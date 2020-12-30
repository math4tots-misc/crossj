package cjx.compiler

import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJAstStatement
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstExpression: CJAstNode {
    case Unit(CJMark)
    case Bool(CJMark, String)
    case Char(CJMark, String)
    case Int(CJMark, String)
    case Double(CJMark, String)
    case String(CJMark, String)
    case Name(CJMark, String)
    case Lambda(CJMark, List[String], CJAstStatement)
    case If(CJMark, CJAstExpression, CJAstExpression, CJAstExpression)
    case Block(CJMark, List[CJAstStatement], Nullable[CJAstExpression])
    case Union(
        CJMark,
        CJAstExpression,
        List[Tuple[String, List[String], CJAstExpression]],
        Nullable[CJAstExpression])
    case Switch(
        CJMark,
        CJAstExpression,
        List[Tuple[List[CJAstExpression], CJAstExpression]],
        Nullable[CJAstExpression])
    case Tuple(CJMark, List[CJAstExpression])
    case List(CJMark, List[CJAstExpression])
    case EmptyList(CJMark, CJAstTypeExpression)
    case Map(CJMark, List[Tuple[CJAstExpression, CJAstExpression]])
    case MutableList(CJMark, List[CJAstExpression])
    case MutableEmptyList(CJMark, List[CJAstExpression])
    case MutableMap(CJMark, List[Tuple[CJAstExpression, CJAstExpression]])
    case Propagate(CJMark, CJAstExpression)
    case LogicalNot(CJMark, CJAstExpression)
    case LogicalAnd(CJMark, CJAstExpression, CJAstExpression)
    case LogicalOr(CJMark, CJAstExpression, CJAstExpression)
    case Is(CJMark, CJAstExpression, CJAstExpression)
    case Await(CJMark, CJAstExpression)
    case StaticField(CJMark, CJAstTypeExpression, String)
    case Field(CJMark, CJAstExpression, String)
    case StaticMethod(CJMark, CJAstTypeExpression, String, List[CJAstTypeExpression], List[CJAstExpression])
    case Method(CJMark, CJAstExpression, String, List[CJAstTypeExpression], List[CJAstExpression])
    case New(CJMark, CJAstTypeExpression, List[CJAstExpression])
    case NewUnion(CJMark, CJAstTypeExpression, String, List[CJAstExpression])

    def mark(self: Self): CJMark {
        return union self {
            case Unit(mark) = mark
            case Bool(mark, s) = mark
            case Char(mark, s) = mark
            case Int(mark, s) = mark
            case Double(mark, s) = mark
            case String(mark, s) = mark
            case Name(mark, name) = mark
            case Lambda(mark, args, body) = mark
            case If(mark, cond, body, other) = mark
            case Block(mark, stmts, inner) = mark
            case Union(mark, target, cases, alt) = mark
            case Switch(mark, target, cases, alt) = mark
            case Tuple(mark, exprs) = mark
            case List(mark, exprs) = mark
            case EmptyList(mark, type) = mark
            case Map(mark, pairs) = mark
            case Propagate(mark, inner) = mark
            case LogicalNot(mark, inner) = mark
            case LogicalAnd(mark, left, right) = mark
            case LogicalOr(mark, left, right) = mark
            case Is(mark, left, right) = mark
            case Await(mark, inner) = mark
            case StaticField(mark, owner, name) = mark
            case Field(mark, owner, name) = mark
            case StaticMethod(mark, owner, name, targs, args) = mark
            case Method(mark, owner, name, targs, args) = mark
            case New(mark, owner, args) = mark
            case NewUnion(mark, owner, name, args) = mark
        }
    }

    def kind(self: Self): String {
        return union self {
            case Unit(mark) = "Unit"
            case Bool(mark, s) = "Bool"
            case Char(mark, s) = "Char"
            case Int(mark, s) = "Int"
            case Double(mark, s) = "Double"
            case String(mark, s) = "String"
            case Name(mark, name) = "Name"
            case Lambda(mark, args, body) = "Lambda"
            case If(mark, cond, body, other) = "If"
            case Block(mark, stmts, inner) = "Block"
            case Union(mark, target, cases, alt) = "Union"
            case Switch(mark, target, cases, alt) = "Switch"
            case Tuple(mark, exprs) = "Tuple"
            case List(mark, exprs) = "List"
            case EmptyList(mark, type) = "EmptyList"
            case Map(mark, pairs) = "Map"
            case Propagate(mark, inner) = "Propagate"
            case LogicalNot(mark, inner) = "LogicalNot"
            case LogicalAnd(mark, left, right) = "LogicalAnd"
            case LogicalOr(mark, left, right) = "LogicalOr"
            case Is(mark, left, right) = "Is"
            case Await(mark, inner) = "Await"
            case StaticField(mark, owner, name) = "StaticField"
            case Field(mark, owner, name) = "Field"
            case StaticMethod(mark, owner, name, targs, args) = "StaticMethod"
            case Method(mark, owner, name, targs, args) = "Method"
            case New(mark, owner, args) = "New"
            case NewUnion(mark, owner, name, args) = "NewUnion"
        }
    }
}
