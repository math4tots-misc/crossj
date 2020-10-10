package crossj.hacks.gameio;

/**
 * Interface for input functionality
 */
public interface InputContext {
    public void setInputHandler(InputHandler handler);

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
}
