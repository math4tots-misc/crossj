package sanity.hacks.ray;

import crossj.Assert;
import crossj.List;
import crossj.Pair;
import crossj.Test;
import crossj.hacks.ray.Matrix;

public final class LinMapTest {

    @Test
    public static void MatrixFromMap() {
        {
            // 1D case
            var f = Matrix.fromMap(List.of(Pair.of(Matrix.tuple(2), Matrix.tuple(8))));
            Assert.equals(f, Matrix.withRows(List.of(4.0)));
        }
        {
            // 4x4 transform matrix (scaling)
            var f = Matrix.fromMap(List.of(Pair.of(Matrix.point(1, 0, 0), Matrix.point(2, 0, 0)),
                    Pair.of(Matrix.point(0, 4, 0), Matrix.point(0, 2, 0)),
                    Pair.of(Matrix.point(0, 0, 1), Matrix.point(0, 0, 1)),
                    Pair.of(Matrix.point(0, 0, 0), Matrix.point(0, 0, 0))));
            Assert.equals(f, Matrix.scaling(2, 0.5, 1));
        }
    }
}
