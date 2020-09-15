package crossj.hacks.ray.gelim;

import crossj.hacks.ray.Matrix;

public final class DeterminantSolver implements GaussianEliminationListener {
    private double denominator = 1;

    @Override
    public void swap(int i, int j) {
        denominator *= -1;
    }

    @Override
    public void scale(int i, double factor) {
        denominator *= factor;
    }

    @Override
    public void addWithFactor(int i, int j, double factor) {
    }

    @Override
    public void finish(boolean invertible) {
        if (!invertible) {
            denominator = 0;
        }
    }

    public static double solve(Matrix matrix) {
        DeterminantSolver solver = new DeterminantSolver();
        GaussianElimination.execute(matrix, solver);
        return solver.denominator == 0 ? 0 : 1 / solver.denominator;
    }
}
