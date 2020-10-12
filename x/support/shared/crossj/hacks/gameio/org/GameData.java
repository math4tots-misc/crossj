package crossj.hacks.gameio.org;

import crossj.base.Disposable;

/**
 * All runtime data for a given game.
 *
 * It owns the GameModel, but also owns other game related resources that the
 * model would not manage (e.g. graphics/audio resources)
 */
public interface GameData<M extends GameModel> extends Disposable {
    public M getModel();

    @Override
    default void dispose() {
        getModel().dispose();
    }
}
