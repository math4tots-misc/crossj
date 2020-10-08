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
     * Returns a graphics context you can use to draw on the screen
     */
    GraphicsContext getGraphics();
}
