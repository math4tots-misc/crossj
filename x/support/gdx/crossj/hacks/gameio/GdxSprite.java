package crossj.hacks.gameio;

import com.badlogic.gdx.graphics.g2d.Sprite;

public final class GdxSprite implements crossj.hacks.gameio.Sprite {
    private final Sprite sprite;

    GdxSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    Sprite getSprite() {
        return sprite;
    }

    @Override
    public double getWidth() {
        return sprite.getWidth();
    }

    @Override
    public double getHeight() {
        return sprite.getHeight();
    }
}
