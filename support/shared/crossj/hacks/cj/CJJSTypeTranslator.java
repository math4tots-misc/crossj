package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Str;

final class CJJSTypeTranslator {
    private final CJAstItemDefinition currentItem;
    private CJIRClassType selfClassType;

    CJJSTypeTranslator(CJAstItemDefinition currentItem) {
        this.currentItem = currentItem;
        this.selfClassType = currentItem.isTrait() ? null : currentItem.getAsSelfClassType();
    }

    private String nameToItemLevelTypeVariableExpression(String shortName) {
        if (currentItem.isTrait()) {
            if (shortName.equals("Self")) {
                return "this";
            } else {
                return "this.TV$" + currentItem.getQualifiedName().replace(".", "$") + "$" + shortName + "()";
            }
        } else {
            return "this.TV$" + shortName;
        }
    }

    public String translateType(CJIRType type) {
        if (type instanceof CJIRVariableType) {
            var variableType = (CJIRVariableType) type;
            if (variableType.isItemLevel()) {
                return nameToItemLevelTypeVariableExpression(variableType.getDefinition().getName());
            } else {
                Assert.that(variableType.isMethodLevel());
                return CJJSTranslator.nameToFunctionLevelTypeVariableName(variableType.getDefinition().getName());
            }
        } else if (type.equals(selfClassType)) {
            return "this";
        } else {
            var classType = (CJIRClassType) type;
            if (classType.getArguments().size() == 0) {
                return CJJSTranslator.qualifiedNameToMetaObjectName(classType.getDefinition().getQualifiedName());
            } else {
                var sb = Str.builder();
                sb.s("new ")
                        .s(CJJSTranslator.qualifiedNameToMetaClassName(classType.getDefinition().getQualifiedName()))
                        .s("(");
                var args = classType.getArguments();
                sb.s(translateType(args.get(0)));
                for (int i = 1; i < args.size(); i++) {
                    sb.s(",").s(translateType(args.get(i)));
                }
                sb.s(")");
                return sb.build();
            }
        }
    }
}
