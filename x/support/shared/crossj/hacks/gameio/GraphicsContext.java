package crossj.hacks.gameio;

import crossj.base.Bytes;

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
}
