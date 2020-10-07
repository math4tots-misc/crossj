package crossj.hacks.gameio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public final class Main extends ApplicationAdapter {
    private final Game game = new crossj.hacks.gameio.placeholder.GamePlaceholder();

    @Override
    public void create() {
        game.init(new GameIO(){
            @Override
            public void requestExit() {
            }

            @Override
            public void requestDraw() {
            }
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
