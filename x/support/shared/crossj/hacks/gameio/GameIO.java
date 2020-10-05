package crossj.hacks.gameio;

/**
 * A mechanism for games to ask things of their host.
 */
public interface GameIO {
    /**
     * Ask the host to exit the program on next update.
     */
    void requestExit();

    /**
     * Ask the host to call redraw on the next frame.
     */
    void requestDraw();
}
