package cjx.compiler

import cjx.compiler.CJToken

trait CJParserMixin {
    def tokens(self: Self): List[CJToken]
    def tell(self: Self): Int
    def seek(self: Self, i: Int)
    def skipsComments(self: Self): Bool

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
}
