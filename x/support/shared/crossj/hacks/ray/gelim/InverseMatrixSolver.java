package crossj.hacks.ray.gelim;

import crossj.Assert;
import crossj.DoubleArray;
import crossj.List;
import crossj.Optional;
import crossj.hacks.ray.math.Matrix;

public final class InverseMatrixSolver implements GaussianEliminationListener {
    private boolean invertible = true;
    private List<DoubleArray> rows;

    private InverseMatrixSolver(int n) {
        rows = Matrix.identity(n).getRows().list();
    }

    @Override
    public void swap(int i, int j) {
        rows.swap(i, j);
    }

    @Override
    public void scale(int i, double factor) {
        rows.get(i).scale(factor);
    }

    @Override
    public void addWithFactor(int i, int j, double factor) {
        rows.get(i).addWithFactor(rows.get(j), factor);
    }

    @Override
    public void finish(boolean invertible) {
        this.invertible = invertible;
    }

    public static Optional<Matrix> invert(Matrix matrix) {
        Assert.withMessage(matrix.isSquare(), "InverseMatrixSolver requires a square matrix");
        InverseMatrixSolver solver = new InverseMatrixSolver(matrix.getC());
        GaussianElimination.execute(matrix, solver);
        return solver.invertible ? Optional.of(Matrix.fromListOfDoubleArrays(solver.rows)) : Optional.empty();
    }
}
