package crossj.hacks.games.tetris;

import crossj.base.IntArray;
import crossj.base.List;
import crossj.base.Pair;
import crossj.base.Set;
import crossj.base.Str;
import crossj.base.Tuple;
import crossj.base.XError;

/**
 * Live Tetris Piece.
 *
 * Contains (row, column) coordinates of the upper-left corner of its shape box and
 * the rotation (# of 90 degree rotations)
 */
public final class Piece {

    /**
     * Possible shapes of a tetris piece.
     *
     * All shapes are assumed to be 4x4, so each shape IntArray is always exactly 16 values long.
     */
    public static final Tuple<IntArray> SHAPES = Tuple.of(
        shapeFromString(
            " x  " +
            " xx " +
            " x  " +
            "    "
        ),
        shapeFromString(
            " x  " +
            " x  " +
            " xx " +
            "    "
        ),
        shapeFromString(
            "  x " +
            "  x " +
            " xx " +
            "    "
        ),
        shapeFromString(
            "  x " +
            " xx " +
            " x  " +
            "    "
        ),
        shapeFromString(
            " x  " +
            " xx " +
            "  x " +
            "    "
        ),
        shapeFromString(
            "  x " +
            "  x " +
            "  x " +
            "  x "
        )
    );

    private static IntArray shapeFromString(String data) {
        return IntArray.fromIterable(Str.toCodePoints(data).map(c -> c == (int) ' ' ? 0 : 1));
    }

    private final IntArray shape;
    private final int row, column, rotation;

    private Piece(IntArray shape, int row, int column, int rotation) {
        this.shape = shape;
        this.row = row;
        this.column = column;
        this.rotation = rotation;
    }

    public static Piece withShape(IntArray shape) {
        return new Piece(shape, 0, 0, 0);
    }

    public Piece rotate(int n) {
        return new Piece(shape, row, column, (rotation + n) % 4);
    }

    public Piece moveTo(int row, int column) {
        return new Piece(shape, row, column, rotation);
    }

    public Piece move(int drow, int dcolumn) {
        return new Piece(shape, row + drow, column + dcolumn, rotation);
    }

    /**
     * Returns a list of (row, column) pairs indicating cells that this piece occupies.
     */
    public List<Pair<Integer, Integer>> cells() {
        var list = List.<Pair<Integer, Integer>>of();
        for (int ri = 0; ri < 4; ri++) {
            for (int ci = 0; ci < 4; ci++) {
                int i;
                switch (rotation) {
                    case 0:
                        i = 4 * ri + ci;
                        break;
                    case 1:
                        i = ri + (3 - ci) * 4;
                        break;
                    case 2:
                        i = (3 - ri) * 4 + (3 - ci);
                        break;
                    case 3:
                        i = (3 - ri) + ci * 4;
                        break;
                    default:
                        throw XError.withMessage("Invalid rotation: " + rotation);
                }
                if (shape.get(i) != 0) {
                    list.add(Pair.of(row + ri, column + ci));
                }
            }
        }
        return list;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String dump() {
        var sb = new StringBuilder();
        var cells = Set.fromIterable(cells());
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                if (cells.contains(Pair.of(row + this.row, column + this.column))) {
                    sb.append("x");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
