package crossj.hacks.gameio;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public final class GdxFont implements BMFont {
    private final BitmapFont font;
    private GlyphLayout layout = null;

    GdxFont(BitmapFont font) {
        this.font = font;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public double getLineHeight() {
        return font.getLineHeight();
    }

    private GlyphLayout getLayout() {
        if (layout == null) {
            layout = new GlyphLayout();
        }
        return layout;
    }

    @Override
    public double getWidth(String text) {
        var layout = getLayout();
        layout.setText(font, text);
        return layout.width;
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
