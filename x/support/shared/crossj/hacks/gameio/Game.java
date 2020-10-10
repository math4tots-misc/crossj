package crossj.hacks.gameio;

import crossj.base.Disposable;

/**
 * An interface for a Game that can be run by a GameHost.
 */
public interface Game extends Disposable {
    default void init(GameIO io) {
    }

    default void pause() {
    }

    default void resume() {
    }

    /**
     * Update the state of the game
     * @param dt seconds since last update. Zero if update was never called before
     */
    default void update(double dt) {
    }

    /**
     * Called when a frame needs to be rendered
     */
    default void render() {
    }

    /**
     * Called when the dimensions of the window changes
     */
    default void resize(int width, int height) {
    }

    @Override
    default void dispose() {
    }
}
