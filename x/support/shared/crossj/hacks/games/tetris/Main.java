package crossj.hacks.games.tetris;

import crossj.hacks.gameio.Game;
import crossj.hacks.gameio.org.GameWithData;

public final class Main {
    public static Game newGame() {
        return GameWithData.of(TetrisData.getDefault(), PlayScene.getInstance());
    }
}
