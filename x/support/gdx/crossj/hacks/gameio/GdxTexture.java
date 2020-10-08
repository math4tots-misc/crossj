package crossj.hacks.gameio;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public final class GdxTexture implements crossj.hacks.gameio.Texture {
    private final Texture texture;

    public GdxTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public crossj.hacks.gameio.Sprite newSprite(int srcX, int srcY, int srcWidth, int srcHeight) {
        return new GdxSprite(new Sprite(texture, srcX, srcY, srcWidth, srcHeight));
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
