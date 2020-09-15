package crossj.hacks.ray.gelim;

public interface GaussianEliminationListener {
    /**
     * Callback for when two rows are swapped
     *
     * R[i] <-> R[j]
     *
     * @param i
     * @param j
     */
    void swap(int i, int j);

    /**
     * Callback for when a row is being scaled by the given factor
     *
     * factor * R[i] -> R[i]
     *
     * @param i
     * @param factor
     */
    void scale(int i, double factor);

    /**
     * Callback for when a row is being added to by a multiple of another row.
     *
     * R[i] + factor * R[j] -> R[i]
     *
     * @param i
     * @param j
     * @param factor
     */
    void addWithFactor(int i, int j, double factor);

    /**
     * Callback for when the eliminiation has finished.
     * @param invertible true iff the original matrix was invertible
     */
    void finish(boolean invertible);
}
