package crossj.hacks.games.tetris;

import crossj.hacks.gameio.Batch;
import crossj.hacks.gameio.GameIO;
import crossj.hacks.gameio.Sprite;
import crossj.hacks.gameio.Texture;
import crossj.hacks.gameio.org.GameData;
import crossj.hacks.image.Color;

public final class TetrisData implements GameData<TetrisModel> {
    private final TetrisModel model = TetrisModel.getDefault();

    private Batch batch;
    private Texture redTexture;
    private Texture whiteTexture;
    private Texture blueTexture;
    private Sprite red;
    private Sprite white;
    private Sprite blue;

    private TetrisData() {
    }

    @Override
    public void setIO(GameIO io) {
        var graphics = io.getGraphics();
        batch = graphics.newBatch();
        redTexture = graphics.newTextureFromColors(1, 1, (x, y) -> Color.rgb(0.8, 0.2, 0.2));
        red = redTexture.newSpriteFromEntireTexture();
        whiteTexture = graphics.newTextureFromColors(1, 1, (x, y) -> Color.rgb(0.8, 0.8, 0.8));
        white = whiteTexture.newSpriteFromEntireTexture();
        blueTexture = graphics.newTextureFromColors(1, 1, (x, y) -> Color.rgb(0.2, 0.2, 0.8));
        blue = blueTexture.newSpriteFromEntireTexture();
    }

    public static TetrisData getDefault() {
        return new TetrisData();
    }

    @Override
    public TetrisModel getModel() {
        return model;
    }

    @Override
    public void dispose() {
        if (redTexture != null) {
            redTexture.dispose();
        }
        if (whiteTexture != null) {
            whiteTexture.dispose();
        }
        if (blueTexture != null) {
            blueTexture.dispose();
        }
    }

    public Batch getBatch() {
        return batch;
    }

    public Sprite getRed() {
        return red;
    }

    public Sprite getWhite() {
        return white;
    }

    public Sprite getBlue() {
        return blue;
    }
}
