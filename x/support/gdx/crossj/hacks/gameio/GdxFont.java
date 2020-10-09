package crossj.hacks.gameio;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public final class GdxFont implements GameFont {
    private final BitmapFont font;

    GdxFont(BitmapFont font) {
        this.font = font;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
