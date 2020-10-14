package crossj.hacks.gameio;

import crossj.base.Disposable;

/**
 * For batch drawing sprites
 */
public interface Batch extends Disposable {

    /**
     * Sets up the batch for drawing
     */
    void begin();

    /**
     * Finishes off rendering
     */
    void end();

    default void draw(Sprite sprite, double x, double y) {
        drawStretched(sprite, x, y, sprite.getWidth(), sprite.getHeight());
    }

    void drawStretched(Sprite sprite, double x, double y, double width, double height);

    void drawText(BMFont font, String text, double x, double y);
}