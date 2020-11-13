package crossj.hacks.deprecated.c;

/**
 * Size and alignment information for a primitive type.
 */
public final class TypeMetrics {
    private final int size, align;
    private final boolean outofline;

    private TypeMetrics(int size, int align, boolean outofline) {
        this.size = size;
        this.align = align;
        this.outofline = outofline;
    }

    public static TypeMetrics of(int size, int align, boolean outofline) {
        return new TypeMetrics(size, align, outofline);
    }

    /**
     * The size that a value of the associated type occupies.
     */
    public int getSize() {
        return size;
    }

    /**
     * The memory alignment for values of the associated type.
     */
    public int getAlign() {
        return align;
    }

    /**
     * Controls placement of constants of the associated type.
     *
     * If true, constants of the associated type cannot appear in dags, such values
     * are placed in anonymous static variables and their values are accessed by
     * fetching the variables
     */
    public boolean getOutofline() {
        return outofline;
    }
}
