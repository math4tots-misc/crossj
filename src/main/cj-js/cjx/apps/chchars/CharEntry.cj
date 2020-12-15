package cjx.apps.chchars

class CharEntry: Repr {
    val _id: Int
    val _char: String
    val _definition: String
    val _pinyin: String

    def id(self: Self): Int {
        return self._id
    }

    def char(self: Self): String {
        return self._char
    }

    def pinyin(self: Self): String {
        return self._pinyin
    }

    def definition(self: Self): String {
        return self._definition
    }

    def repr(self: Self): String {
        return (
            "CharEntry(" +
            self.id() +
            ", " +
            self.char().repr() +
            ", " +
            self.pinyin().repr() +
            ", "  +
            self.definition().repr()
        )
    }
}
