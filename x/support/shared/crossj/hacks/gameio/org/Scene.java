package crossj.hacks.gameio.org;

/**
 * A Scene is a "view" into a GameModel. It knows both how to render information
 * from the GameModel in a specific way, and also how input from the user should
 * affect the given GameModel.
 *
 * Each Scene allows modifying the GameModel in various ways. For example, a
 * "SettingsMenuScene" would display a scene allowing a user to modify settings
 * for a game.
 *
 * Scenes themselves should be mostly stateless. It may carry an ID.
 *
 * Scenes *can* own resources, and they can be released in the 'end' callback.
 * However, doing so would make it difficult to share across different Scenes.
 * Further the resources would be disposed when the Scene is popped, even if a
 * similar resource needing exactly the same resource may be pushed soon after.
 */
public interface Scene<M extends GameModel, D extends GameData<M>> {

    /**
     * Called when the scene is pushed into the scene stack.
     */
    public void start(GameWithData<M, D> game);

    /**
     * Called when the scene is removed from the stack.
     */
    public void end(GameWithData<M, D> game);

    /**
     * Called when window is paused
     */
    public default void pause(GameWithData<M, D> game) {
    }

    /**
     * Called when window is resumed
     */
    public default void resume(GameWithData<M, D> game) {
    }

    /**
     * Called to update the state of the game each frame.
     */
    public void update(GameWithData<M, D> game, double dt);

    /**
     * Called when the game needs to be rendered on the screen each frame. Usually
     * called right after update.
     *
     * The difference between update and render is that when unit testing, the call
     * to 'render' may be skipped.
     */
    public void render(GameWithData<M, D> game);

    /**
     * Called when dimensions of the window changes
     */
    public default void resize(GameWithData<M, D> game, int width, int height) {
    }
}
