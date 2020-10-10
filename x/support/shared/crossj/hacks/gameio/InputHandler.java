package crossj.hacks.gameio;

public interface InputHandler {
    default boolean keyDown(int keycode) {
        return false;
    }

    default public boolean keyUp(int keycode) {
        return false;
    }

    default public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Called when finger or mouse is pressed.
     *
     * Returns true if this event was handled.
     */
    default public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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

    default public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    default public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    default public boolean scrolled(int amount) {
        return false;
    }
}
