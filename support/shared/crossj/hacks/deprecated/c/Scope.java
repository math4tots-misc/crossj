package crossj.hacks.deprecated.c;

/**
 * Basically an enum containing the various scopes in the C language.
 *
 * A local declared at nesting level k has a scope equal to LOCAL + k.
 */
public final class Scope {
    private Scope() {
    }

    public static final int CONSTANTS = 1;
    public static final int LABELS = 2;
    public static final int GLOBAL = 3;
    public static final int PARAM = 4;
    public static final int LOCAL = 5;
}
