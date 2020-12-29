package cjx.compiler

import cjx.compiler.CJAstItemDefinition
import cjx.compiler.CJContext

trait CJContextMixin {
    def global(self: Self): CJContext

    def loadItem(self: Self, itemName: String): Try[CJAstItemDefinition] {
        return self.global().loadItem(itemName)
    }

    def loadedItemNames(self: Self): Iterator[String] {
        return self.global().loadedItemNames()
    }
}
