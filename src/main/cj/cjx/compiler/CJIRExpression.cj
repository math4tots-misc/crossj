package cjx.compiler

import cjx.compiler.CJIRType
import cjx.compiler.CJMark

union CJIRExpression {
    case Unit(CJMark, CJIRType)
    case Bool(CJMark, CJIRType, String)
    case Char(CJMark, CJIRType, String)
    case Int(CJMark, CJIRType, String)
    case Double(CJMark, CJIRType, String)
    case String(CJMark, CJIRType, String)

    def mark(self: Self): CJMark {
        return union self {
            case Unit(mark, type) = mark
            case Bool(mark, type, raw) = mark
            case Char(mark, type, raw) = mark
            case Int(mark, type, raw) = mark
            case Double(mark, type, raw) = mark
            case String(mark, type, raw) = mark
        }
    }

    ## The retrieves the type of the given expression
    def type(self: Self): CJIRType {
        return union self {
            case Unit(mark, type) = type
            case Bool(mark, type, raw) = type
            case Char(mark, type, raw) = type
            case Int(mark, type, raw) = type
            case Double(mark, type, raw) = type
            case String(mark, type, raw) = type
        }
    }
}
