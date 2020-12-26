package cjx.compiler

import cjx.compiler.CJAstCaseDefinition
import cjx.compiler.CJAstMethodDefinition
import cjx.compiler.CJAstFieldDefinition
import cjx.compiler.CJAstExpression
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJIRModifier
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstItemMemberDefinition: CJAstNode {
    case Field(CJAstFieldDefinition)
    case Method(CJAstMethodDefinition)
    case Case(CJAstCaseDefinition)

    def mark(self: Self): CJMark {
        return union self {
            case Field(defn) = defn.mark()
            case Method(defn) = defn.mark()
            case Case(defn) = defn.mark()
        }
    }

    def comment(self: Self): Nullable[String] {
        return union self {
            case Field(defn) = defn.comment()
            case Method(defn) = defn.comment()
            case Case(defn) = defn.comment()
        }
    }

    def modifiers(self: Self): List[CJIRModifier] {
        return union self {
            case Field(defn) = defn.modifiers()
            case Method(defn) = defn.modifiers()
            case Case(defn) = defn.modifiers()
        }
    }

    def name(self: Self): String {
        return union self {
            case Field(defn) = defn.name()
            case Method(defn) = defn.name()
            case Case(defn) = defn.name()
        }
    }

    def isStatic(self: Self): Bool {
        return CJIRModifier.Static in self.modifiers()
    }

    def field(self: Self): Nullable[CJAstFieldDefinition] {
        return union self {
            case Field(defn) = Nullable.Some(defn)
            case Method(defn) = Nullable.None
            case Case(defn) = Nullable.None
        }
    }

    def method(self: Self): Nullable[CJAstMethodDefinition] {
        return union self {
            case Field(defn) = Nullable.None
            case Method(defn) = Nullable.Some(defn)
            case Case(defn) = Nullable.None
        }
    }

    def case_(self: Self): Nullable[CJAstCaseDefinition] {
        return union self {
            case Field(defn) = Nullable.None
            case Method(defn) = Nullable.None
            case Case(defn) = Nullable.Some(defn)
        }
    }
}
