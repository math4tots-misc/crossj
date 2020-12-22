package cjx.compiler

import cjx.compiler.CJIRModifier
import cjx.compiler.CJIRItemKind
import cjx.compiler.CJMark
import cjx.compiler.CJToken

trait CJParserMixin {
    def path(self: Self): String
    def tokens(self: Self): List[CJToken]
    def tell(self: Self): Int
    def seek(self: Self, i: Int)
    def skipsComments(self: Self): Bool

    def mark(self: Self): CJMark {
        val token = self.peek().get()
        return CJMark.of(self.path(), token.line, token.column)
    }

    def peek(self: Self): Nullable[CJToken] {
        val tokens = self.tokens()
        if self.skipsComments() {
            val start = self.tell()
            var j = start
            while j < tokens.size() and tokens.get(j).type == CJToken.tComment {
                j += 1
            }
            if j > start {
                self.seek(j)
            }
        }
        val i = self.tell()
        return if (i < tokens.size()) Nullable.Some(tokens.get(i)) else Nullable.None
    }

    def next(self: Self): Nullable[CJToken] {
        val token = self.peek()
        if token.isPresent() {
            self.seek(self.tell() + 1)
        }
        return token
    }

    def at(self: Self, type: Int): Bool {
        return self.peek().map(t -> t.type == type).getOrDefault(false)
    }

    def consume(self: Self, type: Int): Bool {
        if self.at(type) {
            self.next()
            return true
        } else {
            return false
        }
    }

    def expect(self: Self, type: Int): Try[CJToken] {
        if self.at(type) {
            return Try.Ok(self.next().get())
        } else {
            return Try.fail(
                "Expected " + CJToken.typeToString(type) +
                " but got " +
                self.peek().map(t -> CJToken.typeToString(t.type)).getOrDefault("EOF")
            )
        }
    }

    def expectId(self: Self): Try[String] {
        return Try.Ok(self.expect(CJToken.tId)?.text)
    }

    def expectTypeId(self: Self): Try[String] {
        return Try.Ok(self.expect(CJToken.tTypeId)?.text)
    }

    def expectIdOrTypeId(self: Self): Try[String] {
        return Try.Ok((
            if (self.at(CJToken.tTypeId))
                self.expect(CJToken.tTypeId) else
                self.expect(CJToken.tId)
        )?.text)
    }

    def expectItemKind(self: Self): Try[CJIRItemKind] {
        if self.consume(CJToken.kwUnion) {
            return Try.Ok(CJIRItemKind.Union)
        } else if self.consume(CJToken.kwTrait) {
            return Try.Ok(CJIRItemKind.Trait)
        } else {
            self.expect(CJToken.kwClass)?
            return Try.Ok(CJIRItemKind.Class)
        }
    }

    def parseModifiers(self: Self): List[CJIRModifier] {
        val b = List[CJIRModifier].builder()
        var repeat = true
        while repeat {
            if self.consume(CJToken.kwPrivate) {
                b.add(CJIRModifier.Private)
            } else if self.consume(CJToken.kwPublic) {
                b.add(CJIRModifier.Public)
            } else if self.consume(CJToken.kwNative) {
                b.add(CJIRModifier.Native)
            } else if self.consume(CJToken.kwAsync) {
                b.add(CJIRModifier.Async)
            } else if self.consume(CJToken.kwStatic) {
                b.add(CJIRModifier.Static)
            } else {
                repeat = false
            }
        }
        return b.build()
    }

    def skipComments(self: Self) {
        while self.at(CJToken.tComment) {
            self.next()
        }
    }

    def skipDelimiters(self: Self) {
        while self.at(';'.toInt()) or self.at('\n'.toInt()) {
            self.next()
        }
    }

    ## Skip either a single token or a group (delimited by '[]', '()', or '{}')
    def skipBlob(self: Self) {
        union self.next() {
            case Some(token) {
                switch token.type.toChar() {
                    case '['
                    case '{'
                    case '(' {
                        var depth = 1
                        while depth > 0 and self.peek().isPresent() {
                            switch self.next().get().type.toChar() {
                                case '['
                                case '{'
                                case '(' {
                                    depth += 1
                                }
                                case ']'
                                case '}'
                                case ')' {
                                    depth -= 1
                                }
                                default {}
                            }
                        }
                    }
                    default {}
                }
            }
            case None {}
        }
    }

    def skipTypeOrTraitExpression(self: Self): Bool {
        if self.consume(CJToken.tTypeId) {
            if self.at('['.toInt()) {
                self.skipBlob()
            }
            return true
        } else {
            return false
        }
    }

    def skipModifiers(self: Self) {
        var repeat = true
        while repeat {
            switch self.peek().map(t -> t.type).getOrDefault(0) {
                case CJToken.kwPrivate
                case CJToken.kwPublic
                case CJToken.kwNative {
                    self.next()
                }
                default {
                    repeat = false
                }
            }
        }
    }
}
