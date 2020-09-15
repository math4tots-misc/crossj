package crossj.hacks.ray.main.ch04;

import crossj.IO;
import crossj.List;
import crossj.M;
import crossj.Pair;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;

public final class Main {
    public static void main(String[] args) {
        Bitmap canvas = Bitmap.withDimensions(500, 500);

        // We'll be mostly working in a coordinate system that goes from
        // [-1, 1] in both x and y, with origin in the middle of the screen
        // but on the canvas we'll need to work with [0, 500] in x and y, with y
        // flipped.
        // This transformation matrix should take care of this mapping
        Matrix toCanvasCoordinates = Matrix.fromMap(List.of(
                // (lower-left corner) (-1, -1) => (0, 500)
                Pair.of(Matrix.point(-1, -1, 0), Matrix.point(0, 500, 0)),
                // (upper-left corner) (-1, 1) => (0, 0)
                Pair.of(Matrix.point(-1, 1, 0), Matrix.point(0, 0, 0)),
                // (upper-right corner) (1, 1) => (500, 0)
                Pair.of(Matrix.point(1, 1, 0), Matrix.point(500, 0, 0)),
                // We need a 4th mapping because in reality points are 4D vectors
                // whose 4th value is always 1 (and we work with 4D vectors because
                // 4D linear transformations can represent 3D affine transformations)
                Pair.of(Matrix.point(0, 0, 1), Matrix.point(0, 0, 1))));

        Matrix rotation = Matrix.zRotation(M.TAU / 12);

        Matrix point = Matrix.point(0, 0.5, 0);

        drawPoint(canvas, toCanvasCoordinates.multiply(point));
        for (int i = 0; i < 12; i++) {
            point = rotation.multiply(point);
            drawPoint(canvas, toCanvasCoordinates.multiply(point));
        }

        IO.writeFileBytes("out/ray/ch04.bmp", canvas.toBMPBytes());
    }

    private static void drawPoint(Bitmap canvas, Matrix point) {
        int x = (int) point.getX();
        int y = (int) point.getY();
        canvas.setColor(x, y, Color.rgb(1, 1, 1));
    }
}
