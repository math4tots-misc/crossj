package crossj.hacks.gameio.placeholder;

import crossj.base.IO;
import crossj.hacks.gameio.Batch;
import crossj.hacks.gameio.Game;
import crossj.hacks.gameio.GameIO;
import crossj.hacks.gameio.Sprite;
import crossj.hacks.gameio.Texture;
import crossj.hacks.image.Color;

public final class GamePlaceholder implements Game {
    private GameIO io;
    private Batch batch;
    private Texture texture;
    private Sprite sprite;

    @Override
    public void init(GameIO io) {
        this.io = io;
        var graphics = io.getGraphics();
        batch = graphics.newBatch();
        // working directory is out/gdx/core/assets
        texture = graphics.newTexture(IO.readFileBytes("../../../demo.bmp"));
        sprite = texture.newSpriteFromEntireTexture();
    }

    @Override
    public void render() {
        var graphics = io.getGraphics();
        graphics.clear(Color.GREEN);
        batch.begin();
        batch.draw(sprite, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
