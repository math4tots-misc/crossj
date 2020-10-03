package crossj.hacks.cas;

import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.XError;
import crossj.hacks.cas.algo.IsLiteral;
import crossj.hacks.cas.algo.ReduceToRationalLiteral;
import crossj.hacks.cas.expr.Expression;
import crossj.hacks.cas.expr.Variable;

/**
 * The context when dealing with algebriac expressions.
 *
 * Note that, in hacks.cas, expressions are not really formally typed on their
 * own.
 *
 * In order to be as close as possible to the way "a person would do it",
 * expressions are more or less treated like untyped symbols.
 *
 * So given just an expression like <code>x + 4</code>, it's unclear whether
 * you're working with modular arithmetic, whether x is actually a constant, and
 * whether x is a vector, matrix, or something else.
 */
public final class AlgebraContext {
    private final boolean additionIsCommutative = true;
    private final boolean multiplicationIsCommutative = true;
    private final Map<Variable, Expression> substitutions;

    private AlgebraContext(Map<Variable, Expression> substitutions) {
        for (var key : substitutions.keys()) {
            var expr = substitutions.get(key);
            if (!isLiteral(expr)) {
                throw XError.withMessage("Substitution for " + key + " is not a literal (" + expr + ")");
            }
        }
        this.substitutions = substitutions;
    }

    public static AlgebraContext getDefault() {
        return new AlgebraContext(Map.of());
    }

    public Optional<Expression> getSubstitutionForVariable(Variable variable) {
        return Optional.of(substitutions.getOrNull(variable));
    }

    /**
     * Tries reducing the given expression to a rational literal on a best effort
     * basis.
     */
    public Optional<Expression> reduceToRationalLiteral(Expression expression) {
        return expression.accept(ReduceToRationalLiteral.withContext(this));
    }

    /**
     * Determines wheter the given expression is a "literal".
     *
     * A literal is any expression that contains only universal constants (e.g. pi,
     * e), integer literals, and a combination of elementary functions.
     */
    public boolean isLiteral(Expression expression) {
        return expression.accept(IsLiteral.withContext(this));
    }

    /**
     * Checks whether addition commutative in this context.
     *
     * NOTE, addition is always assumed to be associative.
     */
    public boolean additionIsCommutative() {
        return additionIsCommutative;
    }

    /**
     * Checks whether multiplication is commutative in this context.
     *
     * NOTE, multiplication is always assumed to be associative.
     */
    public boolean multiplicationIsCommutative() {
        return multiplicationIsCommutative;
    }
}
