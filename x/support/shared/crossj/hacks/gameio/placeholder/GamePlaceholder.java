package crossj.hacks.gameio.placeholder;

import crossj.hacks.gameio.Batch;
import crossj.hacks.gameio.Game;
import crossj.hacks.gameio.GameIO;

public final class GamePlaceholder implements Game {
    private GameIO io;
    private Batch batch;

    @Override
    public void init(GameIO io) {
        this.io = io;
        var graphics = io.getGraphics();
    }

    @Override
    public void render() {
        var g = io.getGraphics();
    }
}
