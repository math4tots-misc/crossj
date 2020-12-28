package cjx.compiler

import cjx.compiler.CJParserX
import cjx.compiler.CJAstItemDefinition

class CJContext {
    val _sourceRoots: MutableList[String]
    val _itemDefns: MutableMap[String, CJAstItemDefinition]

    def init(): Self {
        return Self.new(@[], @[:])
    }

    def addSourceRoot(self: Self, sourceRoot: String) {
        self._sourceRoots.add(sourceRoot)
    }

    def setDefinition(self: Self, itemName: String, defn: CJAstItemDefinition) {
        self._itemDefns.put(itemName, defn)
    }

    def forceLoadItem(self: Self, itemName: String): Try[Unit] {
        val relpath = self._itemNameToRelativePathName(itemName)
        for sourceRoot in self._sourceRoots {
            val path = FS.join([sourceRoot, relpath])
            if FS.isfile(path) {
                val data = FS.read(path)?
                val defn = CJParserX.parseString(path, data)?
                self._itemDefns.put(path, defn)
                return Try.Ok(())
            }
        }
        return Try.fail("Item " + itemName + " not found")
    }

    def _itemNameToRelativePathName(self: Self, itemName: String): String {
        return FS.join(itemName.split(".")) + ".cj"
    }
}
