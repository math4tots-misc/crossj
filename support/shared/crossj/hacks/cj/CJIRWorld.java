package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Map;
import crossj.base.Set;
import crossj.base.Tuple;
import crossj.base.XIterable;

/**
 * A CJ world consists of a collection of item definitions.
 *
 * IR = intermediate representation
 */
public final class CJIRWorld {
    private final Map<String, CJAstItemDefinition> map = Map.of();
    private final Map<String, List<CJAstItemDefinition>> traitClosureCache = Map.of();

    /**
     * Classes that are automatically imported from cj.*
     */
    public static final Tuple<String> AUTO_IMPORTED_ITEM_SHORT_NAMES = Tuple.of(
        "Unit",
        "NoReturn",
        "Bool",
        "Int",
        "Double",
        "Char",
        "String",
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
        "Iterable",
        "Iterator",
        "ToBool",
        "ToInt",
        "ToDouble",
        "ToChar",
        "ToString",
        "ToList",
        "ListBuilder",
        "Repr",
        "Eq",
        "Ord",
        "Hash",
        "Assert"
    );

    public void add(CJAstItemDefinition item) {
        map.put(item.getQualifiedName(), item);
    }

    public boolean hasItemWithName(String qualifiedName) {
        return map.containsKey(qualifiedName);
    }

    public CJAstItemDefinition getItem(String qualifiedName) {
        return map.get(qualifiedName);
    }

    public XIterable<CJAstItemDefinition> getAllItems() {
        return List.sorted(map.pairs()).map(pair -> pair.get2());
    }

    public CJAstItemDefinition getItemOrNull(String qualifiedName) {
        return map.getOrNull(qualifiedName);
    }

    public boolean isTrait(String qualifiedName) {
        var item = getItemOrNull(qualifiedName);
        return item != null && item.isTrait();
    }

    public boolean isClass(String qualifiedName) {
        var item = getItemOrNull(qualifiedName);
        return item != null && !item.isTrait();
    }

    public List<CJAstItemDefinition> getTraitClassClosure(String qualifiedName) {
        var result = traitClosureCache.getOrNull(qualifiedName);
        if (result == null) {
            var items = List.<CJAstItemDefinition>of();
            var seen = Set.<String>of(qualifiedName);
            var stack = List.of(getItem(qualifiedName));
            while (stack.size() > 0) {
                var item = stack.pop();
                items.add(item);
                for (var traitExpression : item.getTraits()) {
                    var qualifiedTraitName = item.qualifyName(traitExpression.getName());
                    if (!seen.contains(qualifiedTraitName)) {
                        seen.add(qualifiedTraitName);
                        var trait = getItem(qualifiedTraitName);
                        stack.add(trait);
                    }
                }
            }
            traitClosureCache.put(qualifiedName, items);
            return items;
        } else {
            return result;
        }
    }
}
