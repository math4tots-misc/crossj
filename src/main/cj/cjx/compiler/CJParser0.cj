package cjx.compiler

import cjx.compiler.CJLexer
import cjx.compiler.CJIRTypeParameter
import cjx.compiler.CJIRItemInfo
import cjx.compiler.CJToken
import cjx.compiler.CJParserMixin

class CJParser0: CJParserMixin {

    def parseString(filename: String, data: String): Try[CJIRItemInfo] {
        val tokens = CJLexer.lex(data).addContext(filename)?
        return Self.init(filename, tokens).parseAll()
    }

    val _path: String
    val _tokens: List[CJToken]
    var _i: Int

    def init(filename: String, tokens: List[CJToken]): Self {
        return Self.new(filename, tokens, 0)
    }

    def path(self: Self): String {
        return self._path
    }

    def tokens(self: Self): List[CJToken] {
        return self._tokens
    }

    def tell(self: Self): Int {
        return self._i
    }

    def seek(self: Self, i: Int) {
        self._i = i
    }

    def parseAll(self: Self): Try[CJIRItemInfo] {
        self.expect(CJToken.kwPackage)?
        val pkgParts = @[self.expectId()?]
        while self.consume('.'.toInt()) {
            pkgParts.add(self.expectId()?)
        }
        val pkg = ".".join(pkgParts)
        self.skipDelimiters()
        val imports = List[String].builder()
        while self.consume(CJToken.kwImport) {
            val importParts = @[self.expectId()?]
            while self.consume('.'.toInt()) {
                importParts.add(self.expectIdOrTypeId()?)
            }
            imports.add(".".join(importParts))
            self.skipDelimiters()
        }
        val comment = (if (self.at(CJToken.tComment))
            Nullable.Some(self.expect(CJToken.tComment)?.text) else
            Nullable.None
        )
        self.skipDelimiters()
        val modifiers = self.parseModifiers()
        val kind = self.expectItemKind()?
        val mark = self.mark()
        val shortName = self.expectTypeId()?
        val typeParameters = List[CJIRTypeParameter].builder()
        if self.consume('['.toInt()) {
            while self.at(CJToken.tTypeId) {
                val parameterMark = self.mark()
                val variableName = self.expectTypeId()?
                val typeParameter = CJIRTypeParameter.init(
                    parameterMark,
                    variableName,
                )
                typeParameters.add(typeParameter)
                if self.consume(':'.toInt()) {
                    self.skipTypeOrTraitExpression()
                    while self.consume(','.toInt()) {
                        self.skipTypeOrTraitExpression()
                    }
                }
            }
        }
        return Try.Ok(CJIRItemInfo.init(
            mark,
            pkg,
            imports.build(),
            comment,
            modifiers,
            kind,
            shortName,
            typeParameters.build(),
        ))
    }
}
