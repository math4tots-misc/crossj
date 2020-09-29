package crossj.hacks.cas.expr;

import crossj.Optional;
import crossj.hacks.cas.AlgebraContext;

/**
 * An abstract algebraic expression.
 */
public interface Expression {

    /**
     * Tries to determine if this expression is equal to the given number.
     */
    default Optional<Boolean> isNumber(Number number) {
        return Optional.empty();
    }

    /**
     * Tries to determine if this expression is zero. Returns empty() if the
     * "zero-ness" of the expression cannot be determined.
     */
    default Optional<Boolean> isZero(AlgebraContext ctx) {
        return isNumber(Number.fromInt(0));
    }

    /**
     * Returns true if it can be proven that this expression is equal to zero.
     */
    default boolean isDefinitelyZero(AlgebraContext ctx) {
        return isZero(ctx).getOrElse(false);
    }

    /**
     * Returns true if it can be proven that this expression is not equal to zero.
     */
    default boolean isDefinitelyNotZero(AlgebraContext ctx) {
        return isZero(ctx).map(b -> !b).getOrElse(false);
    }

    /**
     * Tries to determine if this expression is zero. Returns empty() if the
     * "zero-ness" of the expression cannot be determined.
     */
    default Optional<Boolean> isOne(AlgebraContext ctx) {
        return isNumber(Number.fromInt(1));
    }

    /**
     * Returns true if it can be proven that this expression is equal to zero.
     */
    default boolean isDefinitelyOne(AlgebraContext ctx) {
        return isOne(ctx).getOrElse(false);
    }

    /**
     * Returns true if it can be proven that this expression is not equal to zero.
     */
    default boolean isDefinitelyNotOne(AlgebraContext ctx) {
        return isOne(ctx).map(b -> !b).getOrElse(false);
    }
}
