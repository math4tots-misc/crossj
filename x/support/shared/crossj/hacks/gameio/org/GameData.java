package crossj.hacks.gameio.org;

import crossj.base.Disposable;
import crossj.hacks.gameio.GameIO;

/**
 * All runtime data for a given game.
 *
 * It owns the GameModel, but also owns other game related resources that the
 * model would not manage (e.g. graphics/audio resources)
 */
public interface GameData<M extends GameModel> extends Disposable {
    public M getModel();

    /**
     * Allows GameData access to GameIO.
     * This allows GameData instances to lazily load assets and other resources
     * that might requrie GameIO.
     */
    public default void setIO(GameIO io) {
    }

    @Override
    default void dispose() {
        getModel().dispose();
    }
}
