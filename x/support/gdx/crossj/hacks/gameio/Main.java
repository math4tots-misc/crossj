package crossj.hacks.gameio;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import crossj.base.Bytes;
import crossj.hacks.image.Color;

public final class Main implements ApplicationListener, InputProcessor {
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

        public crossj.hacks.gameio.Texture newTextureFromAsset(String assetPath) {
            return new GdxTexture(new Texture(assetPath));
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
    private final AudioContext audio = new AudioContext() {

        @Override
        public Music newMusicFromAsset(String assetPath) {
            var music = Gdx.audio.newMusic(Gdx.files.internal(assetPath));
            return new Music() {
                @Override
                public void dispose() {
                    music.dispose();
                }

                @Override
                public void play() {
                    music.play();
                }

                @Override
                public void stop() {
                    music.stop();
                }

                @Override
                public void pause() {
                    music.pause();
                }

                @Override
                public void setLooping(boolean looping) {
                    music.setLooping(looping);
                }

                @Override
                public void setVolume(double volume) {
                    music.setVolume((float) volume);
                }

                @Override
                public boolean isPlaying() {
                    return music.isPlaying();
                }

                @Override
                public boolean isLooping() {
                    return music.isLooping();
                }
            };
        }

        @Override
        public Sound newSoundFromAsset(String assetPath) {
            var sound = Gdx.audio.newSound(Gdx.files.internal(assetPath));

            return new Sound() {
                @Override
                public void dispose() {
                    sound.dispose();
                }

                @Override
                public void play(double volume) {
                    sound.play((float) volume);
                }
            };
        }
    };
    private final FileSystemContext fileSystem = new FileSystemContext(){
        @Override
        public String readAsset(String path) {
            return Gdx.files.internal(path).readString();
        }

        @Override
        public Bytes readAssetBytes(String path) {
            return Bytes.wrapByteArray(Gdx.files.internal(path).readBytes());
        }
    };
    private final GameIO io = new GameIO() {
        public void requestExit() {
            Gdx.app.exit();
        }

        public GraphicsContext getGraphics() {
            return graphics;
        };

        public AudioContext getAudio() {
            return audio;
        }

        @Override
        public FileSystemContext getFileSystem() {
            return fileSystem;
        }
    };

    @Override
    public void create() {
        game.init(io);
        Gdx.input.setInputProcessor(this);
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

    @Override
    public boolean keyDown(int keycode) {
        return game.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return game.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }
}
