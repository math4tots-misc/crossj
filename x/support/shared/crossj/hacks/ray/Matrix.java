package crossj.hacks.ray;

import crossj.AlmostEq;
import crossj.Assert;
import crossj.DoubleArray;
import crossj.Eq;
import crossj.List;
import crossj.M;
import crossj.Optional;
import crossj.Pair;
import crossj.Range;
import crossj.Str;
import crossj.TypedEq;
import crossj.XError;
import crossj.XIterable;
import crossj.XIterator;
import crossj.hacks.image.Color;
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
public final class Matrix implements AlmostEq<Matrix>, TypedEq<Matrix> {
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

    /**
     * Returns a square identity matrix with the given dimension
     */
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
     * Returns a left-handed rotation around the x-axis in a left-handed coordinate
     * system
     *
     * @param r angle to rotate in radians
     */
    public static Matrix xRotation(double r) {
        return withData(4, 1, 0, 0, 0, 0, M.cos(r), -M.sin(r), 0, 0, M.sin(r), M.cos(r), 0, 0, 0, 0, 1);
    }

    /**
     * Returns a left-handed rotation around the y-axis in a left-handed coordinate
     * system
     *
     * @param r angle to rotate in radians
     */
    public static Matrix yRotation(double r) {
        return withData(4, M.cos(r), 0, M.sin(r), 0, 0, 1, 0, 0, -M.sin(r), 0, M.cos(r), 0, 0, 0, 0, 1);
    }

    /**
     * Returns a left-handed rotation around the z-axis in a left-handed coordinate
     * system
     *
     * @param r angle to rotate in radians
     */
    public static Matrix zRotation(double r) {
        return withData(4, M.cos(r), -M.sin(r), 0, 0, M.sin(r), M.cos(r), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    /**
     * Computes a 4x4 rotation matrix around a given axis
     *
     * @param origin a point on the axis of rotation
     * @param vector a vector indicating the direction of the axis from the origin
     * @param r      angle to rotate in radians
     * @return the matrix describing the rotation
     */
    public static Matrix rotation(Matrix origin, Matrix vector, double r) {
        Assert.withMessage(origin.isPoint(), "Matrix.rotation requires the 'origin' argument to  be a point");
        Assert.withMessage(vector.isVector(), "Matrix.rotation requires the 'vector' argument to be a vector");
        vector = vector.normalize();
        var toOrigin = translation(-origin.getX(), -origin.getY(), -origin.getZ());
        var fromOrigin = translation(origin.getX(), origin.getY(), origin.getZ());
        var cosR = M.cos(r);
        var sinR = M.sin(r);
        var x = vector.getX();
        var y = vector.getY();
        var z = vector.getZ();
        // The rotation assuming 'origin' is (0, 0, 0)
        // from https://en.wikipedia.org/wiki/Rotation_matrix
        var coreRotation = withData(4, cosR + x * x * (1 - cosR), x * y * (1 - cosR) - z * sinR,
                x * z * (1 - cosR) + y * sinR, 0, y * x * (1 - cosR) + z * sinR, cosR + y * y * (1 - cosR),
                y * z * (1 - cosR) - x * sinR, 0, z * x * (1 - cosR) - y * sinR, z * y * (1 - cosR) + x * sinR,
                cosR + z * z * (1 - cosR), 0, 0, 0, 0, 1);
        return fromOrigin.multiply(coreRotation).multiply(toOrigin);
    }

    /**
     * Creates a 3-element column vector of red, green and blue from a color
     */
    public static Matrix fromRGB(Color color) {
        return tuple(color.r, color.g, color.b);
    }

    /**
     * Creates a 4-element column vector of red, green, blue, alpha from a color
     */
    public static Matrix fromRGBA(Color color) {
        return tuple(color.r, color.g, color.b, color.a);
    }

    /**
     * Returns a shearing matrix.
     *
     * From the book "The Ray Tracer Challenge":
     *
     * <blockquote>When applied to a tuple, a shearing transformation changes each
     * component of the tuple in proportion to the other two components. So the x
     * component changes in proportion to y and z, y changes in proportion to x and
     * z and z changes in porportion to x and y.</blockquote>
     *
     * @param xy x moved in proportion to y
     * @param xz x moved in proportion to z
     * @param yx y moved in proportion to x
     * @param yz y moved in proportion to z
     * @param zx z moved in proportion to x
     * @param zy z moved in proportion to y
     * @return the matrix describing this transformation
     */
    public static Matrix shearing(double xy, double xz, double yx, double yz, double zx, double zy) {
        return withData(4, 1, xy, xz, 0, yx, 1, yz, 0, zx, zy, 1, 0, 0, 0, 0, 1);
    }

    /**
     * Returns a new zeroed out matrix with the given dimensions
     *
     * @param nrows
     * @param ncols
     * @return
     */
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

    @SafeVarargs
    public static Matrix withColumns(XIterable<Double>... columns) {
        return fromColumns(List.fromJavaArray(columns));
    }

    public static Matrix fromColumns(XIterable<XIterable<Double>> columns) {
        return fromRows(columns).transpose();
    }

    /**
     * Given n pairs of column vectors (v[i], w[i]), where v[i], w[i] are vectors in
     * R^n returns the linear transformation T such that T(v[i]) = w[i] for all i.
     * TODO: generalize to non-square dimensions, and varying input length
     *
     * @param pairs
     * @return
     */
    public static Matrix fromMap(XIterable<Pair<Matrix, Matrix>> pairs) {
        List<Pair<Matrix, Matrix>> pairList = pairs.iter().list();
        Matrix vmatrix = fromColumns(pairList.iter().map(pair -> pair.get1().data));
        Matrix wmatrix = fromColumns(pairList.iter().map(pair -> pair.get2().data));
        return wmatrix.multiply(vmatrix.inverse());
    }

    /**
     * Creates a new matrix given the number of columns and double values. The
     * number of double values provided should be a multiple of the ncols argument.
     *
     * @param ncols
     * @param data
     * @return
     */
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

    /**
     * Creates a new matrix representing a tuple of given values. In other places,
     * this may be known as a column vector.
     */
    public static Matrix tuple(double... values) {
        return new Matrix(1, DoubleArray.fromJavaDoubleArray(values));
    }

    /**
     * Creates a new matrix representing a 3-D point with given values. The actual
     * resulting matrix is a 4x1 column vector whose 4th term is 1.
     */
    public static Matrix point(double x, double y, double z) {
        return tuple(x, y, z, 1);
    }

    /**
     * Creates a new matrix representing a 3-D vector with given values. The actual
     * resulting matrix is a 4x1 column vector whose 4th term is 0.
     */
    public static Matrix vector(double x, double y, double z) {
        return tuple(x, y, z, 0);
    }

    /**
     * Returns the number of rows in this matrix
     */
    public int getR() {
        return data.size() / ncols;
    }

    /**
     * Returns the number of columns in this matrix
     */
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

    /**
     * Returns a copy of this matrix except with the (0, 0) coordinate replaced with
     * the given value
     */
    public Matrix withX(double newX) {
        Matrix ret = clone();
        ret.set(0, 0, newX);
        return ret;
    }

    /**
     * Returns a copy of this matrix except with the (1, 0) coordinate replaced with
     * the given value
     */
    public Matrix withY(double newY) {
        Matrix ret = clone();
        ret.set(1, 0, newY);
        return ret;
    }

    /**
     * Returns a copy of this matrix except with the (2, 0) coordinate replaced with
     * the given value
     */
    public Matrix withZ(double newZ) {
        Matrix ret = clone();
        ret.set(2, 0, newZ);
        return ret;
    }

    /**
     * Returns a copy of this matrix except with the (3, 0) coordinate replaced with
     * the given value
     */
    public Matrix withW(double newW) {
        Matrix ret = clone();
        ret.set(3, 0, newW);
        return ret;
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
        data.addWithFactor(other.data, 1);
    }

    public Matrix add(Matrix other) {
        Matrix ret = clone();
        ret.inplaceAdd(other);
        return ret;
    }

    private void inplaceSubtract(Matrix other) {
        Assert.equals(ncols, other.ncols);
        data.addWithFactor(other.data, -1);
    }

    public Matrix subtract(Matrix other) {
        Matrix ret = clone();
        ret.inplaceSubtract(other);
        return ret;
    }

    private void inplaceScale(double factor) {
        data.scale(factor);
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
            case 1:
                return data.get(0);
            case 2:
                return data.get(0) * data.get(3) - data.get(1) * data.get(2);
            default:
                return DeterminantSolver.solve(this);
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
     * Part of a fluent interface for creating transformation matrices. Returns a
     * new transform that will apply the given translation after applying this.
     */
    public Matrix thenTranslate(double x, double y, double z) {
        return translation(x, y, z).multiply(this);
    }

    /**
     * Part of a fluent interface for creating transformation matrices. Returns a
     * new transform that will apply the given scaling after applying this.
     */
    public Matrix thenScale(double x, double y, double z) {
        return scaling(x, y, z).multiply(this);
    }

    /**
     * Part of a fluent interface for creating transformation matrices. Returns a
     * new transform that will apply the given rotation after applying this.
     */
    public Matrix thenRotateX(double r) {
        return xRotation(r).multiply(this);
    }

    /**
     * Part of a fluent interface for creating transformation matrices. Returns a
     * new transform that will apply the given rotation after applying this.
     */
    public Matrix thenRotateY(double r) {
        return yRotation(r).multiply(this);
    }

    /**
     * Part of a fluent interface for creating transformation matrices. Returns a
     * new transform that will apply the given rotation after applying this.
     */
    public Matrix thenRotateZ(double r) {
        return zRotation(r).multiply(this);
    }

    /**
     * Euclidean norm taking this matrix as a member of (R x C) Euclidean space
     */
    public double magnitude() {
        return M.pow(magnitudeSquared(), 0.5);
    }

    public double magnitudeSquared() {
        double ret = 0;
        for (double value : data) {
            ret += value * value;
        }
        return ret;
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

    /**
     * Reflects 'this' around the given 'normal' vector. Requires that both 'this'
     * and 'normal' be vectors. Assumes that 'normal' is normalized.
     */
    public Matrix reflectAround(Matrix normal) {
        Assert.withMessage(this.isVector(), "Matrix.reflect requires this to be a normal");
        Assert.withMessage(normal.isVector(), "Matrix.reflect requires the 'normal' argument to be a vector");
        return subtract(normal.scale(2 * dot(normal)));
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
        return rawEquals(obj);
    }

    @Override
    public boolean isEqualTo(Matrix other) {
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
