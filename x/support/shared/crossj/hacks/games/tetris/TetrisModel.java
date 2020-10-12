package crossj.hacks.games.tetris;

import crossj.hacks.gameio.org.GameModel;

public final class TetrisModel implements GameModel {
    private Board board = Board.getDefault();

    private TetrisModel() {
    }

    public static TetrisModel getDefault() {
        return new TetrisModel();
    }

    public Board getBoard() {
        return board;
    }

    /**
     * Updates the model assuming that dt seconds have passed since last tick.
     *
     * @param dt
     */
    public void tick(double dt) {
    }
}
