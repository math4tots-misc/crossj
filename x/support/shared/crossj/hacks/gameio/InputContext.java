package crossj.hacks.gameio;

/**
 * Interface for input functionality
 */
public interface InputContext {
    /**
     * Sets a new input handler for handling user input events.
     *
     * Can be set to null to stop listening to new events.
     */
    public void setInputHandler(InputHandler handler);

    /**
     * Gets the last set input handler
     */
    public InputHandler getInputHandler();

    /**
     * The X position of the current mouse position or the last touch on touch
     * screen devices
     *
     * Origin is the lower-left corner (clicks usually set upper-left corner as
     * origin, but y-axis is flipped to match graphics coordinates)
     */
    public int getX();

    /**
     * The Y position of the current mouse position or the last touch on touch
     * screen devices
     *
     * Origin is the lower-left corner (clicks usually set upper-left corner as
     * origin, but y-axis is flipped to match graphics coordinates)
     */
    public int getY();

    /**
     * Checks if the given button is currently pressed
     */
    public boolean isMouseButtonPressed(int mouseButton);

    /**
     * Checks if the given key is currently pressed
     */
    public boolean isKeyPressed(int keyPressed);

    /**
     * Checks if the given key was pressed in the last frame
     */
    public boolean isKeyJustPressed(int keyPressed);

    /**
     * Checks if the given mouse button was pressed in the last frame
     */
    public boolean isMouseButtonJustPressed(int mouseButton);
}
