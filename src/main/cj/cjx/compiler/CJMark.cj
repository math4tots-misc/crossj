package cjx.compiler

import cjx.compiler.CJToken

class CJMark: Repr {
    val filename: String
    val line: Int
    val column: Int

    def of(filename: String, line: Int, column: Int): Self {
        return Self.new(filename, line, column)
    }

    def fromToken(filename: String, token: CJToken): Self {
        return Self.of(filename, token.line, token.column)
    }

    def repr(self: Self): String {
        return "CJMark.of(" + self.filename.repr() + ", " + self.line + ", " + self.column + ")"
    }
}
