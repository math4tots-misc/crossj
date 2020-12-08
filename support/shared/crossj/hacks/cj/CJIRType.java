package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Try;

public interface CJIRType {

    CJIRType substitute(Map<String, CJIRType> map);

    Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName);

    List<String> getTypeParameterNames();

    boolean isUnion();

    boolean implementsTrait(CJIRTrait trait);

    /**
     * If this type implements a trait with a matching qualified name,
     * returns a reified version of that trait.
     *
     * Otherwise returns empty.
     */
    Optional<CJIRTrait> getImplementingTraitByQualifiedName(String qualifiedName);

    default Optional<CJIRTrait> getImplementingTraitByDefinition(CJAstItemDefinition item) {
        return getImplementingTraitByQualifiedName(item.getQualifiedName());
    }

    Optional<String> getClassTypeQualifiedName();

    default boolean isClassTypeWithName(String qualifiedName) {
        return getClassTypeQualifiedName().map(name -> name.equals(qualifiedName)).getOrElse(false);
    }

    boolean isWrapperType();

    boolean isFunctionType(int argc);

    boolean isTupleType(int argc);

    boolean isNullableType();

    boolean isUnitType();

    boolean isNoReturnType();

    boolean isDerivedFrom(CJAstItemDefinition item);
}
