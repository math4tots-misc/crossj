package crossj.hacks.deprecated.c;

import crossj.base.Tuple;
import crossj.base.XIterable;

public final class CLexerBuilder {
    Tuple<String> keywords;
    Tuple<String> operators;
    boolean cStyleComments;
    boolean hashComments;

    CLexerBuilder(Tuple<String> keywords, Tuple<String> operators, boolean cStyleComments, boolean hashComments) {
        this.keywords = keywords;
        this.operators = operators;
        this.cStyleComments = cStyleComments;
        this.hashComments = hashComments;
    }

    public CLexerBuilder setKeywords(XIterable<String> keywords) {
        this.keywords = Tuple.fromIterable(keywords);
        return this;
    }

    public CLexerBuilder setOperators(XIterable<String> operators) {
        this.operators = Tuple.fromIterable(operators);
        return this;
    }

    public CLexerBuilder setCStyleComments(boolean cStyleComments) {
        this.cStyleComments = cStyleComments;
        return this;
    }

    public CLexerBuilder setHashComments(boolean hashComments) {
        this.hashComments = hashComments;
        return this;
    }

    public CLexer build() {
        return CLexer.fromBuilder(this);
    }
}
