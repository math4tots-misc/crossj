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

    default void render() {
    }

    default void resize(int width, int height) {
    }

    default boolean keyDown(int keycode) {
        return false;
    }

    /**
     * Called when finger is lifted or mouse is released.
     *
     * Returns true if this event was handled.
     */
    default boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    default void dispose() {
    }
}
