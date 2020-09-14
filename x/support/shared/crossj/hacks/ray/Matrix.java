package crossj.hacks.ray;

import crossj.AlmostEq;
import crossj.Assert;
import crossj.DoubleArray;
import crossj.Eq;
import crossj.List;
import crossj.M;
import crossj.Str;

/**
 * Conceptually immutable Matrix.
 *
 * tuples, vectors and points are represented as column vectors.
 *
 * Following the book's definitions:
 * <li>'tuple' is a column vector (i.e. matrix whose dimensions are nx1 for some
 * positive integer n)</li>
 * <li>'vector' is a 'tuple' of 4 elements whose 4th coordinate is 0</li>
 * <li>'point' is a 'tuple' of 4 elements whose 4th coordinate is 1</li>
 *
 * There are some methods that will mutate the Matrix, but they should all be
 * private and are really helpers for constructing new matrices.
 *
 */
public final class Matrix implements AlmostEq<Matrix> {
    private final int ncols;
    private final DoubleArray data;

    private Matrix(int ncols, DoubleArray data) {
        this.ncols = ncols;
        this.data = data;
    }

    @SafeVarargs
    public static Matrix withRows(List<Double>... rows) {
        int nrows = rows.length;
        List<List<Double>> rowList = List.fromJavaArray(rows);
        int maxNCols = rowList.fold(0, (a, b) -> M.imax(a, b.size()));
        int minNCols = rowList.fold(0, (a, b) -> M.imin(a, b.size()));
        Assert.equals(maxNCols, minNCols);
        DoubleArray data = DoubleArray.fromIterable(rowList.flatMap(x -> x));
        Assert.equals(maxNCols * nrows, data.size());
        return new Matrix(maxNCols, data);
    }

    public static Matrix newTuple(double... values) {
        return new Matrix(1, DoubleArray.fromJavaDoubleArray(values));
    }

    public static Matrix newPoint(double x, double y, double z) {
        return newTuple(x, y, z, 1);
    }

    public static Matrix newVector(double x, double y, double z) {
        return newTuple(x, y, z, 0);
    }

    public int getR() {
        return data.size() / ncols;
    }

    public int getC() {
        return ncols;
    }

    public boolean isTuple() {
        return ncols == 1;
    }

    public boolean isVector() {
        return isTuple() && getR() == 4 && getW() == 0;
    }

    public boolean isPoint() {
        return isTuple() && getR() == 4 && getW() == 1;
    }

    public double getX() {
        return get(0, 0);
    }

    public double getY() {
        return get(1, 0);
    }

    public double getZ() {
        return get(2, 0);
    }

    public double getW() {
        return get(3, 0);
    }

    public double get(int row, int column) {
        return data.get(row * ncols + column);
    }

    public void set(int row, int column, double value) {
        data.set(row * ncols + column, value);
    }

    public Matrix clone() {
        return new Matrix(ncols, data.clone());
    }

    private void inplaceAdd(Matrix other) {
        Assert.equals(ncols, other.ncols);
        Assert.equals(data.size(), other.data.size());
        DoubleArray otherData = other.data;
        int len = data.size();
        for (int i = 0; i < len; i++) {
            data.set(i, data.get(i) + otherData.get(i));
        }
    }

    public Matrix add(Matrix other) {
        Matrix ret = clone();
        ret.inplaceAdd(other);
        return ret;
    }

    private void inplaceSubtract(Matrix other) {
        Assert.equals(ncols, other.ncols);
        Assert.equals(data.size(), other.data.size());
        DoubleArray otherData = other.data;
        int len = data.size();
        for (int i = 0; i < len; i++) {
            data.set(i, data.get(i) - otherData.get(i));
        }
    }

    public Matrix subtract(Matrix other) {
        Matrix ret = clone();
        ret.inplaceSubtract(other);
        return ret;
    }

    private void inplaceScale(double factor) {
        int len = data.size();
        for (int i = 0; i < len; i++) {
            data.set(i, factor * data.get(i));
        }
    }

    public Matrix scale(double factor) {
        Matrix ret = clone();
        ret.inplaceScale(factor);
        return ret;
    }

    public Matrix negate() {
        return scale(-1);
    }

    /**
     * Computes the length of a vector/tuple
     */
    public double magnitude() {
        Assert.equals(ncols, 1);
        double ret = 0;
        for (double value : data) {
            ret += value * value;
        }
        return M.pow(ret, 0.5);
    }

    /**
     * Normalizes a vector/tuple
     */
    public Matrix normalize() {
        return scale(1 / magnitude());
    }

    public double dot(Matrix other) {
        Assert.equals(ncols, other.ncols);
        Assert.equals(data.size(), other.data.size());
        int len = data.size();
        DoubleArray otherData = other.data;
        double ret = 0;
        for (int i = 0; i < len; i++) {
            ret += data.get(i) * otherData.get(i);
        }
        return ret;
    }

    public Matrix cross(Matrix b) {
        Assert.that(this.isVector());
        Assert.that(b.isVector());
        return newVector(getY() * b.getZ() - getZ() * b.getY(), getZ() * b.getX() - getX() * b.getZ(),
                getX() * b.getY() - getY() * b.getX());
    }

    public String toString() {
        if (isPoint()) {
            return "Matrix.newPoint(" + getX() + ", " + getY() + ", " + getZ() + ")";
        } else if (isVector()) {
            return "Matrix.newVector(" + getX() + ", " + getY() + ", " + getZ() + ")";
        } else if (isTuple()) {
            return "Matrix.newTuple(" + Str.join(", ", data) + ")";
        } else {
            return "Matrix.withRows(" + Str.join(", ",
                    data.iter().chunk(ncols).map(row -> "List.of(" + Str.join(", ", row) + ")").list()) + ")";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix)) {
            return false;
        }
        Matrix other = (Matrix) obj;
        return ncols == other.ncols && data.equals(other.data);
    }

    @Override
    public boolean almostEquals(Matrix other) {
        if (ncols != other.ncols || data.size() != other.data.size()) {
            return false;
        }
        int len = data.size();
        DoubleArray otherData = other.data;
        for (int i = 0; i < len; i++) {
            if (!Eq.almostForDouble(data.get(i), otherData.get(i))) {
                return false;
            }
        }
        return true;
    }
}
