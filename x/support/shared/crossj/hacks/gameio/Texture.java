package crossj.hacks.gameio;

import crossj.base.Disposable;

/**
 * An image loaded in GPU memory
 *
 * Texture objects must be disposed when they are no longer needed
 */
public interface Texture extends Disposable {

    int getWidth();

    int getHeight();

    /**
     * Create a new sprite using this texture
     * @param srcX
     * @param srcY
     * @param srcWidth
     * @param srcHeight
     * @return
     */
    Sprite newSprite(int srcX, int srcY, int srcWidth, int srcHeight);

    default Sprite newSpriteFromEntireTexture() {
        return newSprite(0, 0, getWidth(), getHeight());
    }
}
