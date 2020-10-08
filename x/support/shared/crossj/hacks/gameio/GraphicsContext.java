package crossj.hacks.gameio;

import crossj.base.Bytes;
import crossj.hacks.image.Color;

/**
 * Context used for rendering stuff to the screen.
 */
public interface GraphicsContext {
    /**
     * Creates a new Texture from PNG, JPEG or BMP bytes.
     * @param data
     * @return
     */
    Texture newTexture(Bytes data);

    /**
     * Creates a new Texture from a PNG, JPEG or BMP file from a bundled asset.
     * @param assetPath
     * @return
     */
    Texture newTextureFromAsset(String assetPath);

    /**
     * Create a new SpriteBatch with the given buffer size.
     * @param size
     * @return
     */
    Batch newBatchWithSize(int size);

    /**
     * Create a new SpriteBatch with a buffer size of 1000.
     * @return
     */
    default Batch newBatch() {
        return newBatchWithSize(1000);
    }

    /**
     * Clear the entire screen with the given color
     * @param color
     */
    void clear(Color color);
}
