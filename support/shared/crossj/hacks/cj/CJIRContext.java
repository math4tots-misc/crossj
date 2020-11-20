package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;

/**
 * Annotation context. For resolving types and looking up symbols.
 */
public final class CJIRContext {
    final CJIRWorld world;
    CJAstItemDefinition currentItem = null;
    CJAstMethodDefinition currentMethod = null;
    Map<String, CJIRTrait> shortNameToTraitMap = null;
    Map<String, CJIRType> itemLevelTypeMap = null;
    Map<String, CJIRType> methodLevelTypeMap = null;
    List<Map<String, CJIRType>> localVariableStack = null;

    CJIRContext(CJIRWorld world) {
        this.world = world;
    }

    void enterItem(CJAstItemDefinition item) {
        currentItem = item;
        shortNameToTraitMap = Map.of();
        itemLevelTypeMap = Map.of();
    }

    void exitItem() {
        currentItem = null;
        shortNameToTraitMap = null;
        itemLevelTypeMap = null;
        exitMethod();
    }

    void enterMethod(CJAstMethodDefinition method) {
        currentMethod = method;
        methodLevelTypeMap = Map.of();
        localVariableStack = List.of(Map.of());
    }

    void exitMethod() {
        currentMethod = null;
        methodLevelTypeMap = null;
        localVariableStack = null;
    }

    void enterBlock() {
        localVariableStack.add(Map.of());
    }

    void exitBlock() {
        localVariableStack.pop();
    }

    Optional<CJIRType> getVariableType(String variableName) {
        for (int i = localVariableStack.size() - 1; i >= 0; i--) {
            var map = localVariableStack.get(i);
            var type = map.getOrNull(variableName);
            if (type != null) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
