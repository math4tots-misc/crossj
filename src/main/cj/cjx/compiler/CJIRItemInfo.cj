package cjx.compiler

import cjx.compiler.CJIRTypeParameter
import cjx.compiler.CJMark

class CJIRItemInfo {
    val _mark: CJMark
    val _packageName: String
    val _imports: List[String]
    val _shortName: String
    val _typeParameters: List[CJIRTypeParameter]

    def init(
            mark: CJMark,
            packageName: String,
            imports: List[String],
            shortName: String,
            typeParameters: List[CJIRTypeParameter]): Self {
        return Self.new(
            mark,
            packageName,
            imports,
            shortName,
            typeParameters,
        )
    }

    def mark(self: Self): CJMark {
        return self._mark
    }

    def packageName(self: Self): String {
        return self._packageName
    }

    def shortName(self: Self): String {
        return self._shortName
    }

    def fullName(self: Self): String {
        return self._packageName + "." + self._shortName
    }
}
