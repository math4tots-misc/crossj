package crossj.hacks.gameio.org;

import crossj.base.Disposable;

/**
 * Data structure containing all information about a game, including
 * both the state of all entities in the game as well as user settings.
 */
public interface GameModel extends Disposable {
    /**
     * Update the game model, given 'dt' seconds have passed.
     * @param dt
     */
    public void tick(double dt);
}
