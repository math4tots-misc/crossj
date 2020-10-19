package crossj.hacks.games.hanjahero;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public final class Main implements ApplicationListener {

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
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // List<Integer> list = List.of(1, 2, 3);
        // IO.println(list);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
