package crossj.hacks.ray.gelim;

import crossj.Assert;
import crossj.DoubleArray;
import crossj.List;
import crossj.M;
import crossj.hacks.ray.Matrix;

/**
 * Performs gaussian elimination to reduced row echelon form.
 * @param <Listener>
 */
public final class GaussianElimination<Listener extends GaussianEliminationListener> {
    private final Listener listener;
    private final List<DoubleArray> rows;

    private GaussianElimination(Listener listener, List<DoubleArray> rows) {
        // todo-lowpri: allow non-square matrices
        Assert.equalsWithMessage(rows.size(), rows.get(0).size(), "GaussianEliminiation requires a square matrix");
        this.listener = listener;
        this.rows = rows;
    }

    public static <Listener extends GaussianEliminationListener> void execute(Matrix matrix, Listener listener) {
        GaussianElimination<Listener> gelim = new GaussianElimination<Listener>(listener, matrix.getRows().list());
        gelim.run();
    }

    private void swap(int i, int j) {
        if (i != j) {
            rows.swap(i, j);
            listener.swap(i, j);
        }
    }

    private void addWithFactor(int dest, int src, double factor) {
        if (factor != 0) {
            rows.get(dest).addWithFactor(rows.get(src), factor);
            listener.addWithFactor(dest, src, factor);
        }
    }

    private void finish(boolean invertible) {
        listener.finish(invertible);
    }

    private void run() {
        int nrows = rows.size();
        int ncols = rows.get(0).size();

        for (int c = 0; c < ncols; c++) {
            // make rows[c, c] = 1 and rows[r, c] = 0 for all other r

            // for numerical stability, find the row with largest coefficient for
            // column c among those remaining (i.e. find the pivot)
            int bestRow = 0;
            double bestValue = 0;
            for (int r = c; r < nrows; r++) {
                double value = M.abs(rows.get(r).get(c));
                if (value > bestValue) {
                    bestRow = r;
                    bestValue = value;
                }
            }
            if (bestValue == 0) {
                // this column only has zeros
                finish(false);
                return;
            }

            // rearrange so that row c has the largest coefficient for column c
            swap(bestRow, c);

            // clear out column c for all rows c+1 to end.
            double srcCoef = rows.get(c).get(c);
            for (int r = c + 1; r < nrows; r++) {
                double dstCoef = rows.get(r).get(c);
                addWithFactor(r, c, -dstCoef/srcCoef);
            }
        }

        // At this point, we're in row echelon form.
        // We no longer need to actually update the row values --
        // we can deduce what the remaining operations are without further modifications.
        for (int c = ncols - 1; c >= 0; c--) {
            // clear out all entries [r, c] for r < c
            double srcCoef = rows.get(c).get(c);
            for (int r = 0; r < c; r++) {
                double dstCoef = rows.get(r).get(c);
                double factor = -dstCoef/srcCoef;
                if (factor != 0) {
                    listener.addWithFactor(r, c, factor);
                }
            }
        }

        // Now we should have a diagonal matrix -- just apply scaling
        for (int i = 0; i < ncols; i++) {
            listener.scale(i, 1 / rows.get(i).get(i));
        }

        finish(true);
        return;
    }
}
