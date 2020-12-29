package cjx.compiler

class CJJSStringBuilder {
    val _b: StringBuilder
    var _indent: Int

    def init(): Self {
        return Self.new(String.builder(), 0)
    }

    def line(self: Self, s: String) {
        self.lineStart(s)
        self.lineEnd("")
    }

    def lineStart(self: Self, s: String) {
        for i = 0; i < self._indent; i += 1 {
            self._b.add("  ")
        }
        self._b.add(s)
    }

    def lineBody(self: Self, s: String) {
        self._b.add(s)
    }

    def lineEnd(self: Self, s: String) {
        self._b.add(s)
        self._b.add("\n")
    }

    def indent(self: Self) {
        self._indent += 1
    }

    def dedent(self: Self) {
        self._indent -= 1
    }

    def build(self: Self): String {
        return self._b.build()
    }
}
