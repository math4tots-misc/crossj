package crossj.hacks.gameio.org;

import crossj.base.Disposable;

/**
 * Data structure containing all information about a game, including
 * both the state of all entities in the game as well as user settings.
 */
public interface GameModel extends Disposable {
    @Override
    default void dispose() {
    }
}
