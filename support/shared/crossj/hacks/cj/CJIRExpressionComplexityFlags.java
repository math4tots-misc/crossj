package crossj.hacks.cj;

/**
 * A set of flags that describes an expression and its subexpressions.
 *
 * This flag summarizes how "complex" an expression is.
 *
 * This allows code generators to know ahead of time what strategy to use for
 * translating the expression.
 */
public final class CJIRExpressionComplexityFlags {
    /**
     * Flag indicating that this expression has been annotated for complexity.
     *
     * If no other flags are set, it means that: - there are no lambda expressions
     * in this expression, - there are no block/compound expressions in this
     * expression, - there are no "complex jumps" (note, this logical and/or do not
     * count as "complex jumps")
     */
    public static final int NONE = 1;

    /**
     * A lambda expression whose body is a single return statement with a return
     * expression with a NONE|SIMPLE_LAMBDA complexity rating.
     *
     * In general, when emitting backend code, simple lambdas can be emitted in-line
     */
    public static final int SIMPLE_LAMBDA = 2;

    /**
     * At least one lambda appears in this expression, and it may not be simple.
     */
    public static final int COMPLEX_LAMBDA = 4;

    /**
     * Contains a block/compound expression.
     *
     * This means that statements appear in this expression (not including
     * statements that appear in lambdas)
     */
    public static final int BLOCK = 8;

    /**
     * Contains the use of the error propagation operator (i.e. postfix '?').
     *
     * This potentially causes a return in the middle of evaluating an expression.
     * For many target languages that don't natively support this, such expressions
     * can't be translated into expressions in the target language.
     */
    public static final int ERROR_PROPAGATION = 16;

    /**
     * Contains a union-match expression
     */
    public static final int UNION_MATCH = 32;

    /**
     * Contains a switch-match expression
     */
    public static final int SWITCH_MATCH = 64;
}
