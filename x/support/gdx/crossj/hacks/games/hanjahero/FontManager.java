package crossj.hacks.games.hanjahero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import crossj.base.Disposable;

public final class FontManager implements Disposable {
    private final FreeTypeFontGenerator fontGenerator;
    private final BitmapFont hangulFont;

    private FontManager() {
        this.fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("noto-serif-kr.otf"));

        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 64;

        // hangul syllables (range 0xAC00 to 0xD7AF)
        StringBuilder sb = new StringBuilder();
        for (int ch = 0xAC00; ch <= 0xD7AF; ch++) {
            sb.append((char) ch);
        }
        parameter.characters = sb.toString();

        hangulFont = fontGenerator.generateFont(parameter);
    }

    public static FontManager newDefault() {
        return new FontManager();
    }

    @Override
    public void dispose() {
        fontGenerator.dispose();
        hangulFont.dispose();
    }

    public BitmapFont getHangulFont() {
        return hangulFont;
    }
}
