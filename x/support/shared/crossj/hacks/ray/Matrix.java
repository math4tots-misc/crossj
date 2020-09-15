package crossj.hacks.ray;

import crossj.AlmostEq;
import crossj.Assert;
import crossj.DoubleArray;
import crossj.Eq;
import crossj.List;
import crossj.M;
import crossj.Range;
import crossj.Str;
import crossj.XError;
import crossj.XIterable;
import crossj.XIterator;
import crossj.Optional;
import crossj.hacks.ray.gelim.DeterminantSolver;
import crossj.hacks.ray.gelim.InverseMatrixSolver;

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
    private static final Matrix ID2 = identityNoCache(2);
    private static final Matrix ID3 = identityNoCache(3);
    private static final Matrix ID4 = identityNoCache(4);

    private final int ncols;
    private final DoubleArray data;

    private Matrix(int ncols, DoubleArray data) {
        Assert.divides(ncols, data.size());
        this.ncols = ncols;
        this.data = data;
    }

    public static Matrix identity(int n) {
        switch (n) {
            case 2:
                return ID2;
            case 3:
                return ID3;
            case 4:
                return ID4;
            default:
                return identityNoCache(n);
        }
    }

    private static Matrix identityNoCache(int n) {
        Matrix ret = withDimensions(n, n);
        for (int i = 0; i < n; i++) {
            ret.set(i, i, 1);
        }
        return ret;
    }

    /**
     * Returns a new 4x4 translation matrix
     */
    public static Matrix translation(double x, double y, double z) {
        return withData(4, 1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0, 0, 0, 1);
    }

    /**
     * Returns a 4x4 scaling matrix
     */
    public static Matrix scaling(double x, double y, double z) {
        return withData(4, x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1);
    }

    /**
     * Returns a left-handed rotation around the x-axis in a left-handed coordinate system
     * @param r angle to rotate in radians
     * @return
     */
    public static Matrix xRotation(double r) {
        return withData(4, 1, 0, 0, 0, 0, M.cos(r), -M.sin(r), 0, 0, M.sin(r), M.cos(r), 0, 0, 0, 0, 1);
    }

    /**
     * Returns a left-handed rotation around the y-axis in a left-handed coordinate system
     * @param r angle to rotate in radians
     * @return
     */
    public static Matrix yRotation(double r) {
        return withData(4, M.cos(r), 0, M.sin(r), 0, 0, 1, 0, 0, -M.sin(r), 0, M.cos(r), 0, 0, 0, 0, 1);
    }

    /**
     * Returns a left-handed rotation around the z-axis in a left-handed coordinate system
     * @param r angle to rotate in radians
     * @return
     */
    public static Matrix zRotation(double r) {
        return withData(4, M.cos(r), -M.sin(r), 0, 0, M.sin(r), M.cos(r), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    public static Matrix withDimensions(int nrows, int ncols) {
        DoubleArray data = DoubleArray.withSize(nrows * ncols);
        return new Matrix(ncols, data);
    }

    @SafeVarargs
    public static Matrix withRows(XIterable<Double>... rows) {
        List<DoubleArray> rowList = List.fromJavaArray(rows).map(row -> DoubleArray.fromIterable(row));
        return fromListOfDoubleArrays(rowList);
    }

    public static Matrix fromRows(XIterable<XIterable<Double>> rows) {
        List<DoubleArray> rowList = List.fromIterable(rows).map(row -> DoubleArray.fromIterable(row));
        return fromListOfDoubleArrays(rowList);
    }

    public static Matrix withData(int ncols, double... data) {
        return new Matrix(ncols, DoubleArray.fromJavaDoubleArray(data));
    }

    public static Matrix fromListOfDoubleArrays(List<DoubleArray> rowList) {
        int nrows = rowList.size();
        int maxNCols = rowList.fold(0, (a, b) -> M.imax(a, b.size()));
        int minNCols = rowList.fold(rowList.get(0).size(), (a, b) -> M.imin(a, b.size()));
        Assert.equals(maxNCols, minNCols);
        DoubleArray data = DoubleArray.fromIterable(rowList.flatMap(x -> x));
        Assert.equals(maxNCols * nrows, data.size());
        return new Matrix(maxNCols, data);
    }

    public static Matrix tuple(double... values) {
        return new Matrix(1, DoubleArray.fromJavaDoubleArray(values));
    }

    public static Matrix point(double x, double y, double z) {
        return tuple(x, y, z, 1);
    }

    public static Matrix vector(double x, double y, double z) {
        return tuple(x, y, z, 0);
    }

    public int getR() {
        return data.size() / ncols;
    }

    public int getC() {
        return ncols;
    }

    public boolean isSquare() {
        return getC() == getR();
    }

    public DoubleArray getRow(int r) {
        return data.slice(ncols * r, ncols * (r + 1));
    }

    public XIterator<DoubleArray> getRows() {
        return Range.upto(getR()).map(r -> getRow(r));
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

    private void set(int row, int column, double value) {
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

    public Matrix multiply(Matrix other) {
        int len = ncols;
        Assert.equalsWithMessage(len, other.getR(), "matrix multiplication dimension check");
        int newR = getR();
        int newC = other.getC();
        DoubleArray arr = DoubleArray.withSize(newR * newC);
        int j = 0;
        for (int r = 0; r < newR; r++) {
            for (int c = 0; c < newC; c++) {
                double value = 0;
                for (int i = 0; i < len; i++) {
                    value += get(r, i) * other.get(i, c);
                }
                arr.set(j, value);
                j++;
            }
        }
        return new Matrix(newC, arr);
    }

    public Matrix transpose() {
        int nrows = getR();
        DoubleArray arr = DoubleArray.withSize(data.size());
        int i = 0;
        for (int c = 0; c < ncols; c++) {
            for (int r = 0; r < nrows; r++) {
                arr.set(i, get(r, c));
                i++;
            }
        }
        return new Matrix(nrows, arr);
    }

    public double determinant() {
        Assert.equalsWithMessage(ncols, getR(), "only square matrices have determinants");
        DoubleArray data = this.data;
        switch (ncols) {
            case 1: return data.get(0);
            case 2: return data.get(0) * data.get(3) - data.get(1) * data.get(2);
            default: return DeterminantSolver.solve(this);
        }
    }

    public Optional<Matrix> tryInverse() {
        return InverseMatrixSolver.invert(this);
    }

    public Matrix inverse() {
        return tryInverse().orThrow(() -> XError.withMessage("this matrix is not invertible"));
    }

    public boolean isInvertible() {
        return determinant() != 0;
    }

    public Matrix submatrix(int skipR, int skipC) {
        int nrows = getR();
        int ncols = getC();
        DoubleArray data = DoubleArray.withSize((nrows - 1) * (ncols - 1));
        int i = 0;
        for (int r = 0; r < nrows; r++) {
            if (r == skipR) {
                continue;
            }
            for (int c = 0; c < ncols; c++) {
                if (c == skipC) {
                    continue;
                }
                data.set(i, get(r, c));
                i++;
            }
        }
        return new Matrix(ncols - 1, data);
    }

    public double minor(int skipR, int skipC) {
        return submatrix(skipR, skipC).determinant();
    }

    /**
     * Euclidean norm taking this matrix as a member of (R x C) Euclidean space
     */
    public double magnitude() {
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
        return vector(getY() * b.getZ() - getZ() * b.getY(), getZ() * b.getX() - getX() * b.getZ(),
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
            return "Matrix.withRows("
                    + Str.join(", ", data.iter().chunk(ncols).map(row -> "List.of(" + Str.join(", ", row) + ")").list())
                    + ")";
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
