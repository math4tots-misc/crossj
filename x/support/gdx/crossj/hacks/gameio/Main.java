package crossj.hacks.gameio;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import crossj.base.Bytes;
import crossj.hacks.image.Color;

public final class Main implements ApplicationListener {
    private final Game game = new crossj.hacks.gameio.placeholder.GamePlaceholder();
    private final GraphicsContext graphics = new GraphicsContext() {
        public crossj.hacks.gameio.Texture newTexture(Bytes data) {
            var buffer = data.getUnderlyingByteBuffer();
            Pixmap pixmap = null;
            if (buffer.hasArray()) {
                var array = buffer.array();
                var offset = buffer.arrayOffset();
                var len = buffer.limit();
                pixmap = new Pixmap(array, offset, len);
            } else {
                var array = new byte[data.size()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = (byte) data.getI8(i);
                }
                pixmap = new Pixmap(array, 0, array.length);
            }
            return new GdxTexture(new Texture(pixmap));
        }

        @Override
        public Batch newBatchWithSize(int size) {
            return new GdxBatch(new SpriteBatch(size));
        }

        @Override
        public void clear(Color color) {
            Gdx.gl.glClearColor((float) color.r, (float) color.g, (float) color.b, (float) color.a);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
    };
    private final GameIO io = new GameIO() {
        public void requestExit() {
            Gdx.app.exit();
        }

        public GraphicsContext getGraphics() {
            return graphics;
        };
    };

    @Override
    public void create() {
        game.init(io);
    }

    @Override
    public void render() {
        game.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.render();
    }

    @Override
    public void resize(int width, int height) {
        game.resize(width, height);
    }

    @Override
    public void pause() {
        game.pause();
    }

    @Override
    public void resume() {
        game.resume();
    }

    @Override
    public void dispose() {
        game.dispose();
    }
}
