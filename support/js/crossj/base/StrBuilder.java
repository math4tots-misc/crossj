package crossj.base;

/**
 * Javascript's StringBuilder wrapper
 */
public final class StrBuilder {
    StrBuilder() {
    }

    native public String build();

    native public StrBuilder obj(Object object);

    native public StrBuilder c(char c);

    native public StrBuilder codePoint(int codePoint);

    native public StrBuilder i(int i);

    native public StrBuilder d(double d);

    native public StrBuilder s(String s);
}
