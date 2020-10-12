package crossj.hacks.games.tetris;

import crossj.base.IO;
import crossj.hacks.gameio.Batch;
import crossj.hacks.gameio.MouseButton;
import crossj.hacks.gameio.Sprite;
import crossj.hacks.gameio.org.GameWithData;
import crossj.hacks.gameio.org.Scene;
import crossj.hacks.image.Color;

public final class PlayScene implements Scene<TetrisModel, TetrisData> {
    private static final PlayScene INSTANCE = new PlayScene();

    private PlayScene() {
    }

    public static PlayScene getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(GameWithData<TetrisModel, TetrisData> game) {
        IO.println("Starting play scene");
    }

    @Override
    public void end(GameWithData<TetrisModel, TetrisData> game) {
    }

    @Override
    public void update(GameWithData<TetrisModel, TetrisData> game, double dt) {
        game.getModel().tick(dt);
    }

    @Override
    public void render(GameWithData<TetrisModel, TetrisData> game) {
        var data = game.getData();
        var batch = data.getBatch();

        batch.begin();
        renderBoard(game, batch);
        batch.end();
    }

    private void renderBoard(GameWithData<TetrisModel, TetrisData> game, Batch batch) {
        var data = game.getData();
        var io = game.getIO();
        var input = io.getInput();
        var graphics = io.getGraphics();

        var model = game.getModel();
        var board = model.getBoard();

        var color = input.isMouseButtonPressed(MouseButton.LEFT) ? Color.rgb(0.7, 0.2, 0.2) : Color.rgb(0.4, 0.4, 0.4);
        graphics.clear(color);

        var nRows = board.getHeight();
        var nCols = board.getWidth();

        var pixelWidth = graphics.getWidth();
        var pixelHeight = graphics.getHeight();

        var boardPixelWidth = pixelWidth / 2;

        var cellPixelWidth = boardPixelWidth / nCols;
        var cellPixelHeight = pixelHeight / nRows;
        for (int row = 0; row < nRows; row++) {
            var pixelY = pixelHeight - (row + 1) * cellPixelHeight;
            for (int column = 0; column < nCols; column++) {
                var pixelX = column * cellPixelWidth;
                var cell = board.getCell(row, column);
                Sprite cellColor = null;
                if (row < 2) {
                    cell = 2;
                } else if ((row + column) % 2 == 0) {
                    cell = 1;
                }
                switch (cell) {
                    case 0: cellColor = data.getWhite(); break;
                    case 1: cellColor = data.getBlue(); break;
                    default: cellColor = data.getRed();
                }
                batch.drawStretched(cellColor, pixelX, pixelY, cellPixelWidth, cellPixelHeight);
            }
        }
    }
}
