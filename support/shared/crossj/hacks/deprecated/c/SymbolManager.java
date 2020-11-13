package crossj.hacks.deprecated.c;

/**
 * Holds all the symbol tables needed by an instance of a C compiler.
 */
public final class SymbolManager {
    private int level = Scope.GLOBAL;
    @SuppressWarnings("unused")
    private SymbolTable constants = SymbolTable.of(Scope.CONSTANTS, null);
    @SuppressWarnings("unused")
    private SymbolTable externals = SymbolTable.of(Scope.GLOBAL, null);
    private SymbolTable globals = SymbolTable.of(Scope.GLOBAL, null);
    private SymbolTable identifiers = globals;
    private SymbolTable labels = null;
    private SymbolTable types = SymbolTable.of(Scope.GLOBAL, null);

    // for generating labels
    private int label = 1;

    /**
     * Gets the currently active scope.
     *
     * See the Scope class for possible values.
     */
    public int getLevel() {
        return level;
    }

    public void enterScope() {
        level++;
    }

    public void exitScope() {
        if (types.getLevel() == level) {
            types = types.getPrevious();
        }
        if (identifiers.getLevel() == level) {
            identifiers = identifiers.getPrevious();
        }
        level--;
    }

    /**
     * Generates a run of n labels
     */
    public int genLabels(int n) {
        label += n;
        return label - n;
    }

    /**
     * Get the Symbol associated with the given label, or create and add it as
     * needed
     */
    public Symbol findLabel(int lab) {
        var labelstr = "" + lab;
        var symbol = labels.getOrNull(labelstr);
        if (symbol != null) {
            return symbol;
        }
        // If a symbol is not already found, we create and insert one.
        symbol = Symbol.of(labelstr, Scope.LABELS, null, null, 0, null);
        symbol.setGenerated(true);
        symbol.setLabelInfo(LabelInfo.of(label, null));
        labels.put(labelstr, symbol);
        return symbol;
    }
}
