package cjx.compiler

import cjx.compiler.CJToken
import cjx.compiler.CJIRItemInfo

class CJIRWorld {
    val _tokensMap: MutableMap[String, List[CJToken]]
    val _itemMap: MutableMap[String, CJIRItemInfo]

    def init(): Self {
        return Self.new(@[:], @[:])
    }
}
