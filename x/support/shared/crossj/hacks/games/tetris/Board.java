package crossj.hacks.games.tetris;

import crossj.base.List;

/**
 * Tetris board.
 *
 * (0, 0) is the upper-left corner.
 */
public final class Board {
    public static final int DEFAULT_WIDTH = 10;
    public static final int DEFAULT_HEIGHT = 20;

    private final int width;
    private final int height;
    private List<List<Integer>> rows;

    private Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.rows = List.ofSize(height, () -> List.ofSize(width, () -> 0));
    }

    public static Board getDefault() {
        return new Board(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCell(int row, int column) {
        return rows.get(row).get(column);
    }

    public void setCell(int row, int column, int value) {
        rows.get(row).set(column, value);
    }

    public boolean isEmptyCell(int row, int column) {
        return row >= 0 && row < height && column >= 0 && column < width  && getCell(row, column) == 0;
    }

    public boolean canFitPiece(Piece piece) {
        for (var cell : piece.cells()) {
            if (!isEmptyCell(cell.get1(), cell.get2())) {
                return false;
            }
        }
        return true;
    }

    public void fillWithPiece(Piece piece, int color) {
        for (var cell : piece.cells()) {
            setCell(cell.get1(), cell.get2(), color);
        }
    }

    public void clearFilledRows() {
        var remainingRows = rows.map(row -> row.iter().all(i -> i > 0) ? null : row).filter(row -> row != null);
        var newRows = List.ofSize(height - remainingRows.size(), () -> List.ofSize(width, () -> 0));
        rows = List.of(newRows, remainingRows).flatMap(r -> r);
    }
}
