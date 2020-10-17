package crossj.hacks.games.demo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Demo libgdx game
 */
public final class Main implements ApplicationListener {

    private FreeTypeFontGenerator fontGenerator = null;
    private BitmapFont latinFont = null;
    private BitmapFont hangulFont = null;
    private BitmapFont hanjaFont = null;
    private SpriteBatch batch = null;

    private void initHangulFont() {
        if (hangulFont == null) {
            var parameter = new FreeTypeFontParameter();
            parameter.size = 64;

            // hangul syllables (range 0xAC00 to 0xD7AF)
            var sb = new StringBuilder();
            for (int ch = 0xAC00; ch <= 0xD7AF; ch++) {
                sb.append((char) ch);
            }
            parameter.characters = sb.toString();

            hangulFont = fontGenerator.generateFont(parameter);
        }
    }

    private void initHanjaFont() {
        if (hanjaFont == null) {
            var parameter = new FreeTypeFontParameter();
            parameter.size = 64;
            parameter.characters = Gdx.files.internal("ch-chars.txt").readString();
            hanjaFont = fontGenerator.generateFont(parameter);
        }
    }

    @Override
    public void create() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("noto-serif-kr.otf"));
        batch = new SpriteBatch();
        latinFont = new BitmapFont();
        initHangulFont();
        initHanjaFont();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        latinFont.draw(batch, "hello world", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        hangulFont.draw(batch, "한글 hello 是", Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        hanjaFont.draw(batch, "之不一人以有了为道是子的来大也十其上二而中曰下于三得在",
                Gdx.graphics.getWidth() / 8, 3 * Gdx.graphics.getHeight() / 4);
        hanjaFont.draw(batch, "年我他王说事见将者去日天州出后又自此个时无军太这与月",
                Gdx.graphics.getWidth() / 8, 3 * Gdx.graphics.getHeight() / 4 - hanjaFont.getLineHeight());
        hanjaFont.draw(batch, "所家如知你里公行可使到四至",
                Gdx.graphics.getWidth() / 8, 3 * Gdx.graphics.getHeight() / 4 - 2 * hanjaFont.getLineHeight());
        batch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        latinFont.dispose();
        hanjaFont.dispose();
        hangulFont.dispose();
        fontGenerator.dispose();
    }

}
