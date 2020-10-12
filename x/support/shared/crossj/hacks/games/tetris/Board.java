package crossj.hacks.games.tetris;

import crossj.base.IntArray;

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
    private final IntArray arr;

    private Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.arr = IntArray.withSize(width * height);
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

    private int getIndex(int row, int column) {
        return row * width + column;
    }

    public int getCell(int row, int column) {
        return arr.get(getIndex(row, column));
    }

    public void setCell(int row, int column, int value) {
        arr.set(getIndex(row, column), value);
    }
}
