package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.XError;

/**
 * Annotation context. For resolving types and looking up symbols.
 */
final class CJIRContext {
    final CJIRWorld world;
    CJAstItemDefinition currentItem = null;
    CJAstMethodDefinition currentMethod = null;
    Map<String, CJAstItemDefinition> shortNameToItemMap = null;
    Map<String, CJAstTypeParameter> itemLevelTypeMap = null;
    Map<String, CJAstTypeParameter> methodLevelTypeMap = null;
    List<Map<String, CJIRLocalVariableInfo>> localVariableStack = null;
    private CJIRClassType currentSelfType = null;
    private CJIRType declaredReturnType = null;
    private List<CJIRType> returnTypeStack = null;

    CJIRContext(CJIRWorld world) {
        this.world = world;
    }

    void enterItem(CJAstItemDefinition item) {
        currentItem = item;
        shortNameToItemMap = Map.fromIterable(CJIRWorld.AUTO_IMPORTED_ITEM_SHORT_NAMES.iter()
                .map(shortName -> Pair.of(shortName, world.getItem("cj." + shortName))));
        itemLevelTypeMap = Map.of();
        currentSelfType = null;
    }

    void exitItem() {
        currentItem = null;
        shortNameToItemMap = null;
        itemLevelTypeMap = null;
        currentSelfType = null;
        exitMethod();
    }

    void enterMethod(CJAstMethodDefinition method) {
        currentMethod = method;
        methodLevelTypeMap = Map.of();
        localVariableStack = List.of(Map.of());
        declaredReturnType = null;
        returnTypeStack = List.of();
    }

    void exitMethod() {
        currentMethod = null;
        methodLevelTypeMap = null;
        localVariableStack = null;
        declaredReturnType = null;
        returnTypeStack = null;
    }

    void enterBlock() {
        localVariableStack.add(Map.of());
    }

    void exitBlock() {
        localVariableStack.pop();
    }

    void enterLambda(CJIRType expectedReturnType) {
        enterBlock();
        returnTypeStack.add(declaredReturnType);
        declaredReturnType = expectedReturnType;
    }

    void exitLambda() {
        declaredReturnType = returnTypeStack.pop();
        exitBlock();
    }

    boolean isItemLevelTypeVariable(String name) {
        return itemLevelTypeMap != null && itemLevelTypeMap.containsKey(name);
    }

    boolean isMethodLevelTypeVariable(String name) {
        return methodLevelTypeMap != null && methodLevelTypeMap.containsKey(name);
    }

    boolean isTypeVariable(String name) {
        return isItemLevelTypeVariable(name) || isMethodLevelTypeVariable(name);
    }

    public static CJIRAnnotatorException err0(String message, CJMark... marks) {
        return CJIRAnnotatorException.fromParts(message, List.fromJavaArray(marks));
    }

    CJIRTrait resolveTraitExpression(CJAstTraitExpression traitExpression) {
        if (!traitExpression.hasAsIsTrait()) {
            var name = traitExpression.getName();
            var item = shortNameToItemMap.getOrNull(name);
            if (item == null) {
                throw err0("Item " + name + " not found (should be a trait)", traitExpression.getMark());
            }
            if (!item.isTrait()) {
                throw err0(name + " is not a trait", traitExpression.getMark());
            }
            if (item.getTypeParameters().size() != traitExpression.getArguments().size()) {
                int argc = traitExpression.getArguments().size();
                var arge = item.getTypeParameters().size();
                throw err0("Expected " + arge + " type args but got " + argc, traitExpression.getMark(),
                        item.getMark());
            }
            var trait = new CJIRTrait(item, traitExpression.getArguments().map(arg -> resolveTypeExpression(arg)));
            traitExpression.setAsIsTrait(trait);
        }
        return traitExpression.getAsIsTrait();
    }

    Optional<CJAstItemDefinition> getItem(String shortName) {
        return shortNameToItemMap.getOptional(shortName);
    }

    CJIRType resolveTypeExpression(CJAstTypeExpression typeExpression) {
        if (!typeExpression.hasAsIsType()) {
            var mark = typeExpression.getMark();
            var name = typeExpression.getName();
            CJIRType type = null;
            if (name.equals("Fn")) {
                var typeArguments = typeExpression.getArguments();
                switch (typeArguments.size()) {
                    case 0:
                        throw err0("Fn requires at least a return type", mark);
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        name = "Fn" + (typeArguments.size() - 1);
                        break;
                    default:
                        throw err0("Too many arguments to Fn", mark);
                }
                var item = shortNameToItemMap.get(name);
                type = new CJIRClassType(item, typeArguments.map(t -> resolveTypeExpression(t)));
            } else if (name.equals("Tuple")) {
                var typeArguments = typeExpression.getArguments();
                switch (typeArguments.size()) {
                    case 0:
                    case 1:
                        throw err0("Tuple requires at least two type arguments", mark);
                    case 2:
                    case 3:
                    case 4:
                        name = "Tuple" + typeArguments.size();
                        break;
                    default:
                        throw err0("Too many arguments to Tuple", mark);
                }
                var item = shortNameToItemMap.get(name);
                type = new CJIRClassType(item, typeArguments.map(t -> resolveTypeExpression(t)));
            } else if (isItemLevelTypeVariable(name)) {
                Assert.equals(typeExpression.getArguments().size(), 0);
                var definition = itemLevelTypeMap.get(name);
                type = new CJIRVariableType(definition, true);
            } else if (isMethodLevelTypeVariable(name)) {
                Assert.equals(typeExpression.getArguments().size(), 0);
                var definition = methodLevelTypeMap.get(name);
                type = new CJIRVariableType(definition, false);
            } else if (!currentItem.isTrait() && typeExpression.getName().equals("Self")) { // self
                if (currentSelfType == null) {
                    currentSelfType = currentItem.getAsSelfClassType();
                }
                type = currentSelfType;
            } else { // class
                var optItem = shortNameToItemMap.getOptional(name);
                if (optItem.isEmpty()) {
                    throw err0("Item " + name + " not found (should be a class)", typeExpression.getMark());
                }
                var item = optItem.get();
                if (item.isTrait()) {
                    throw err0(name + " is a trait, not a class", typeExpression.getMark());
                }
                if (item.getTypeParameters().size() != typeExpression.getArguments().size()) {
                    int argc = typeExpression.getArguments().size();
                    var arge = item.getTypeParameters().size();
                    throw err0("Expected " + arge + " type args but got " + argc, typeExpression.getMark(),
                            item.getMark());
                }
                type = new CJIRClassType(item, typeExpression.getArguments().map(arg -> resolveTypeExpression(arg)));
            }
            typeExpression.setAsIsType(type);
        }
        return typeExpression.getAsIsType();
    }

    void declareImport(String shortName, String qualifiedItemName, CJMark mark) {
        if (shortNameToItemMap.containsKey(shortName)
                && !shortNameToItemMap.get(shortName).getQualifiedName().equals(qualifiedItemName)) {
            throw err0(shortName + " is already declared in this scope", mark);
        }
        var item = world.getItemOrNull(qualifiedItemName);
        if (item == null) {
            throw err0("Item " + qualifiedItemName + " not found", mark);
        }
        shortNameToItemMap.put(shortName, item);
    }

    void declareTypeVariable(CJAstTypeParameter typeParameter) {
        if (isTypeVariable(typeParameter.getName())) {
            throw err0("Type variable " + typeParameter.getName() + " is already defined in this scope",
                    typeParameter.getMark());
        }
        if (methodLevelTypeMap != null) {
            methodLevelTypeMap.put(typeParameter.getName(), typeParameter);
        } else {
            itemLevelTypeMap.put(typeParameter.getName(), typeParameter);
        }
    }

    Optional<CJIRLocalVariableInfo> getVariableInfo(String variableName) {
        for (int i = localVariableStack.size() - 1; i >= 0; i--) {
            var map = localVariableStack.get(i);
            var type = map.getOrNull(variableName);
            if (type != null) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    void declareVariable(CJMark mark, boolean mutable, String variableName, CJIRType variableType) {
        if (getVariableInfo(variableName).isPresent()) {
            throw err0("Variable " + variableName + " is already defined in this scope", mark);
        } else {
            localVariableStack.last().put(variableName, new CJIRLocalVariableInfo(mutable, variableType));
        }
    }

    public CJIRType getDeclaredReturnType() {
        if (declaredReturnType == null) {
            if (currentMethod == null) {
                throw XError.withMessage("return type accessed outside of method");
            }
            declaredReturnType = currentMethod.getReturnType().getAsIsType();
        }
        return declaredReturnType;
    }
}
