package crossj.hacks.games.hanjahero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import crossj.base.Disposable;

public final class CJKFontManager implements Disposable {
    private final FreeTypeFontGenerator fontGenerator;
    // private final BitmapFont hangulFont;
    private final BitmapFont font;

    private CJKFontManager() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("noto-serif-kr.otf"));
        {
            var parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.incremental = true;
            parameter.size = 64;
            font = fontGenerator.generateFont(parameter);
        }
    }

    public static CJKFontManager newDefault() {
        return new CJKFontManager();
    }

    @Override
    public void dispose() {
        // hangulFont.dispose();
        font.dispose();
        fontGenerator.dispose();
    }

    public BitmapFont getFont() {
        return font;
    }
}
