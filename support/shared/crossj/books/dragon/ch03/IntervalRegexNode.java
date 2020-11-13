package crossj.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.Optional;

final class IntervalRegexNode implements RegexNode {
    public static final int BINDING_PRECEDENCE = StarRegexNode.BINDING_PRECEDENCE;

    private final RegexNode inner;
    private final int min;
    private final int max;

    IntervalRegexNode(RegexNode inner, int min, int max) {
        Assert.withMessage(min >= 0, "min in IntervalRegexNode must be non-negative");
        Assert.withMessage(max == -1 || min <= max, "min must be less than or equal to max in IntervalRegexNode");
        this.inner = inner;
        this.min = min;
        this.max = max;
    }

    @Override
    public int getBindingPrecedence() {
        return BINDING_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        var suffix = min == max ? "{" + min + "}" : max == -1 ? "{" + min + ",}" : "{" + min + "," + max + "}";
        return RegexNodeHelper.wrap(inner, BINDING_PRECEDENCE) + suffix;
    }

    @Override
    public void buildBlock(NFABuilder builder, int startState, int acceptState) {
        if (min == max || max == -1) {
            // if max == -1, it means repeat
            if (min == 0) {
                if (max == -1) {
                    // this is basically just kleene star
                    new StarRegexNode(inner).buildBlock(builder, startState, acceptState);
                    return;
                } else {
                    // this is basically just a zero length match
                    builder.connect(startState, Optional.empty(), acceptState);
                    return;
                }
            }
            // we can assume from here that min >= 1
            int rep = min;
            for (int i = 0; i < rep; i++) {
                builder.buildBlock(inner, -1, -1);
            }
        }
    }
}
