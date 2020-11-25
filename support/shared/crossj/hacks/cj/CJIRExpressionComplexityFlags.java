package crossj.hacks.cj;


/**
 * A set of flags that describes an expression and its subexpressions.
 *
 * This flag summarizes how "complex" an expression is.
 *
 * This allows code generators to know ahead of time what strategy to use
 * for translating the expression.
 */
public final class CJIRExpressionComplexityFlags {
    public static final int NONE = 1;
    public static final int LAMBDA = 2;
}
