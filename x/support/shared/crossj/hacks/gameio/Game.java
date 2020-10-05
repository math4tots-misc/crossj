package crossj.hacks.gameio;

/**
 * An interface for a Game that can be run by a GameHost.
 */
public interface Game {
    /**
     * Status flag. If set, requests the host to exit.
     */
    public static final int STATUS_EXIT = 2;

    /**
     * Status flag. If set requests the host to redraw to the screen.
     *
     * NOTE: This is just a hint. Even if the flag returned by update does not have
     * this flag set, if the host's environment clears the screen, 'draw' may still
     * be called if deemed needed by the host.
     */
    public static final int STATUS_DRAW = 4;

    default void init(GameIO io) {
    }

    default void pause() {
    }

    default void resume() {
    }

    default void update(double dt) {
    }

    default void draw(Brush brush) {
    }

    default void resize(int width, int height) {
    }

    default void keydown(String key) {
    }

    default void keyup(String key) {
    }

    default void click(int button, int x, int y) {
    }
}
