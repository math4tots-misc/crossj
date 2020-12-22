package cjx.compiler

import cjx.compiler.CJMark

trait CJAstNode {
    def mark(self: Self): CJMark
}
