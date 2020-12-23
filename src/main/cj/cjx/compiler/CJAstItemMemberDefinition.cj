package cjx.compiler

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

    def mark(self: Self): CJMark {
        return union self {
            case Field(defn) = defn.mark()
            case Method(defn) = defn.mark()
        }
    }

    def comment(self: Self): Nullable[String] {
        return union self {
            case Field(defn) = defn.comment()
            case Method(defn) = defn.comment()
        }
    }

    def modifiers(self: Self): List[CJIRModifier] {
        return union self {
            case Field(defn) = defn.modifiers()
            case Method(defn) = defn.modifiers()
        }
    }

    def name(self: Self): String {
        return union self {
            case Field(defn) = defn.name()
            case Method(defn) = defn.name()
        }
    }

    def isStatic(self: Self): Bool {
        return CJIRModifier.Static in self.modifiers()
    }

    def field(self: Self): Nullable[CJAstFieldDefinition] {
        union self {
            case Field(defn) {
                return Nullable.Some(defn)
            }
            case Method(defn) {
                return Nullable.None
            }
        }
    }

    def method(self: Self): Nullable[CJAstMethodDefinition] {
        union self {
            case Field(defn) {
                return Nullable.None
            }
            case Method(defn) {
                return Nullable.Some(defn)
            }
        }
    }
}
