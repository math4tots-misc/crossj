package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Map;
import crossj.base.XIterable;

/**
 * A CJ world consists of a collection of item definitions.
 *
 * IR = intermediate representation
 */
public final class CJIRWorld {
    private final Map<String, CJAstItemDefinition> map = Map.of();

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
}
