package cjx.compiler

import cjx.compiler.CJParserX
import cjx.compiler.CJAstItemDefinition

class CJContext {
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

    def _forceLoadItem(self: Self, itemName: String): Try[CJAstItemDefinition] {
        val relpath = self._itemNameToRelativePathName(itemName)
        for sourceRoot in self._sourceRoots {
            val path = FS.join([sourceRoot, relpath])
            if FS.isfile(path) {
                val data = FS.read(path)?
                val defn = CJParserX.parseString(path, data)?
                return Try.Ok(defn)
            }
        }
        return Try.fail("Item " + itemName + " not found")
    }

    def forceLoadItem(self: Self, itemName: String): Try[CJAstItemDefinition] {
        val defn = self._forceLoadItem(itemName)?
        self._itemDefns.put(itemName, defn)
        return Try.Ok(defn)
    }

    def loadItem(self: Self, itemName: String): Try[CJAstItemDefinition] {
        return union self._itemDefns.getOrNull(itemName) {
            case Some(defn) = Try.Ok(defn)
            case None = self.forceLoadItem(itemName)
        }
    }

    def loadAutoImportedItems(self: Self): Try[Unit] {
        for shortName in Self.autoImportedItemShortNames {
            self.loadItem("cj." + shortName)?
        }
        return Try.Ok(())
    }

    def loadItemDeps(self: Self, itemName: String): Try[List[CJAstItemDefinition]] {
        val out = List[CJAstItemDefinition].builder()
        {
            # Load all items regardless of cycles
            val stack = @[self.loadItem(itemName)?]
            for shortName in Self.autoImportedItemShortNames {
                stack.add(self.loadItem("cj." + shortName)?)
            }
            val seen = MutableSet.of(stack.map(defn -> defn.fullName()).toList())
            while stack.toBool() {
                val defn = stack.pop()
                out.add(defn)
                for imp in defn.imports() {
                    val name = imp.qualifiedName()
                    if name not in seen {
                        seen.add(name)
                        stack.add(self.loadItem(name)?)
                    }
                }
            }
        }
        return Try.Ok(out.build())
    }

    ## Given an item definition, get a list of item (trait?) names that the given
    ## item implements. This is used to determinine the topological order of
    def _listBaseItems(self: Self, defn: CJAstItemDefinition): Try[List[String]] {
        val map = self._computeLocalItemMap(defn)
        val traitExprs = defn.traits().map(pair -> pair.get0())
        val out = List[String].builder()
        for traitExpr in traitExprs {
            val shortName = traitExpr.name()
            union map.getOrNull(shortName) {
                case Some(fullName) {
                    out.add(fullName)
                }
                case None {
                    return (Try[List[String]].fail("Trait alias " + shortName + " not found")
                        .addContext(traitExpr.mark().toString()))
                }
            }
        }
        return Try.Ok(out.build())
    }

    ## Maps short names to their corresponding full item names within an item definition.
    ## This is basically all the mappings implied by the imports.
    def _computeLocalItemMap(self: Self, defn: CJAstItemDefinition): Map[String, String] {
        return (
            Self.autoImportedItemShortNames.map(
                shortName -> (shortName, "cj." + shortName)).toMap() +
            defn.imports().map(imp -> (imp.alias(), imp.qualifiedName())).toMap())
    }

    def _itemNameToRelativePathName(self: Self, itemName: String): String {
        return FS.join(itemName.split(".")) + ".cj"
    }
}
