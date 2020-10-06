package crossj.hacks.gameio;

/**
 * An interface for a Game that can be run by a GameHost.
 */
public interface Game {
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
