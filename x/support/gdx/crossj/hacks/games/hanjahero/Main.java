package crossj.hacks.games.hanjahero;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import crossj.base.IO;
import crossj.base.List;

public final class Main implements ApplicationListener {
    private SpriteBatch batch;
    private FontManager fontManager;

    @Override
    public void create() {
        switch (Gdx.app.getType()) {
            case Desktop:
                // Make it as similar to mobile as possible
                Gdx.graphics.setWindowedMode(1080 / 3, 1920 / 3);
                break;
            default:
                break;
        }

        batch = new SpriteBatch();
        fontManager = FontManager.newDefault();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        var list = List.of(1, 2, 3);
        IO.println(list);

        BitmapFont font = fontManager.getHangulFont();

        batch.begin();
        font.draw(batch, "한글", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() * 3 / 4);
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
        fontManager.dispose();
    }
}
