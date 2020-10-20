package crossj.hacks.c;

import crossj.base.Map;

/**
 * A table of all symbols in a given scope.
 */
public final class SymbolTable {
    private final int level;
    private final SymbolTable previous;
    private final Map<String, Symbol> map = Map.of();
    private Symbol all;

    private SymbolTable(int level, SymbolTable previous) {
        this.level = level;
        this.previous = previous;
        this.all = previous == null ? null : previous.all;
    }

    public static SymbolTable of(int level, SymbolTable previous) {
        return new SymbolTable(level, previous);
    }

    /**
     * The scope for this table.
     *
     * See the Scope class for possible values.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Pointer to the SymbolTable for the outer scope.
     *
     * May return null if this table represents an outermost scope (e.g. Scope.GLOBAL or Scope.CONSTANTS).
     */
    public SymbolTable getPrevious() {
        return previous;
    }

    /**
     * Returns the "last" Symbol in this table.
     *
     * Following the up field in each symbol, you can iterate over all
     * Symbols visible from this scope, including all shadowed symbols.
     */
    public Symbol getAll() {
        return all;
    }

    /**
     * Tries to look up a symbol with the given name.
     *
     * Returns null if not found.
     */
    public Symbol getOrNull(String name) {
        for (var table = this; table != null; table = table.previous) {
            var symbol = table.map.getOrNull(name);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    public void put(String key, Symbol symbol) {
        map.put(key, symbol);
        all = symbol;
    }
}
