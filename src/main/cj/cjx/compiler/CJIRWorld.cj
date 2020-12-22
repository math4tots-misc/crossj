package cjx.compiler

import cjx.compiler.CJToken
import cjx.compiler.CJIRItemInfo

class CJIRWorld {
    ## Classes that are automatically imported from cj.*
    static val autoImportedItemShortNames: List[String] = [
        "Unit",
        "NoReturn",
        "Bool",
        "Int",
        "Double",
        "Char",
        "String",
        "StringBuilder",
        "Buffer",
        "MutableList",
        "MutableMap",
        "MutableSet",
        "Collection",
        "Clone",
        "List",
        "Map",
        "Set",
        "Fn0",
        "Fn1",
        "Fn2",
        "Fn3",
        "Fn4",
        "Tuple2",
        "Tuple3",
        "Tuple4",
        "Try",
        "Error",
        "Nullable",
        "IO",
        "AIO",
        "FS",
        "Iterable",
        "Iterator",
        "ToBool",
        "ToInt",
        "ToDouble",
        "ToChar",
        "ToString",
        "ToList",
        "ListBuilder",
        "MapBuilder",
        "SetBuilder",
        "Promise",
        "Math",
        "Repr",
        "Eq",
        "Ord",
        "Hash",
        "Assert"
    ]

    val _tokensMap: MutableMap[String, List[CJToken]]
    val _itemMap: MutableMap[String, CJIRItemInfo]

    def init(): Self {
        return Self.new(@[:], @[:])
    }
}
