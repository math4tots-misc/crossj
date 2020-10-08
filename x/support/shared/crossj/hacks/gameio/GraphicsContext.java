package crossj.hacks.gameio;

import crossj.base.Bytes;
import crossj.hacks.image.Color;

/**
 * Context used for rendering stuff to the screen.
 */
public interface GraphicsContext {
    /**
     * Creates a new SpriteBatch from PNG, JPEG or BMP bytes.
     * @param data
     * @return
     */
    Texture newTexture(Bytes data);

    Batch newBatchWithSize(int size);

    /**
     * Clear the entire screen with the given color
     * @param color
     */
    void clear(Color color);

    default Batch newBatch() {
        return newBatchWithSize(1000);
    }
}
