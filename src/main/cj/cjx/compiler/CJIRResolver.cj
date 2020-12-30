package cjx.compiler

import cjx.compiler.CJIRItemKind
import cjx.compiler.CJIRType
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJAstExpression
import cjx.compiler.CJAstStatement
import cjx.compiler.CJLocalContext

## Resolve types, statements and expressions
class CJIRResolver {
    val _lctx: CJLocalContext

    def init(lctx: CJLocalContext): Self {
        return Self.new(lctx)
    }

    def resolveType(self: Self, typeExpr: CJAstTypeExpression): Try[CJIRType] {
        val itemDefn = self._lctx.item()
        val name = typeExpr.name()

        # check for Self type
        if name == "Self" {
            if typeExpr.args().toBool() {
                return (
                    Try[CJIRType].fail("Self cannot have any type arguments")
                    .addContext(typeExpr.mark().toString()))
            }
            return union itemDefn.kind() {
                case Trait = Try.Ok(CJIRType.Self(itemDefn))
                default = Try.Ok(CJIRType.reifyClassWithOwnTypeParameters(itemDefn))
            }
        }

        # check for imported classes
        for imp in itemDefn.imports() {
            if imp.alias() == name {
                val defn = self._lctx.loadItem(imp.qualifiedName())?
                val args = Try.list(typeExpr.args().iter().map(e -> self.resolveType(e)))?
                return Try.Ok(CJIRType.Class(defn, args))
            }
        }

        # check type variables
        for typeParameter in itemDefn.typeParameters() {
            if typeParameter.name() == name {
                if typeExpr.args().toBool() {
                    return (
                        Try[CJIRType].fail("Type variables cannot have any type arguments")
                        .addContext(typeExpr.mark().toString()))
                }
                return Try.Ok(CJIRType.Variable(name, itemDefn, self._lctx._method))
            }
        }
        union self._lctx._method {
            case Some(methodDefn) {
                for typeParameter in methodDefn.typeParameters() {
                    if typeParameter.name() == name {
                        if typeExpr.args().toBool() {
                            return (
                                Try[CJIRType].fail(
                                    "Type variables cannot have any type arguments")
                                .addContext(typeExpr.mark().toString()))
                        }
                        return Try.Ok(CJIRType.Variable(name, itemDefn, self._lctx._method))
                    }
                }
            }
            case None {}
        }
        return Try[CJIRType].fail("Type constructor " + name + " not found")
    }

    def resolveStatement(self: Self, stmt: CJAstStatement): Try[CJAstStatement] {
        return union stmt {
            default = IO.panic("TODO unhandled resolveStatement: " + stmt.kind())
        }
    }

    def resolveExpression(self: Self, expr: CJAstExpression): Try[CJAstExpression] {
        return union expr {
            default = IO.panic("TODO unhandled resolveExpression: " + expr.kind())
        }
    }
}
