package crossj.hacks.gameio.placeholder;

import crossj.base.IO;
import crossj.hacks.gameio.Batch;
import crossj.hacks.gameio.Game;
import crossj.hacks.gameio.GameIO;
import crossj.hacks.gameio.Music;
import crossj.hacks.gameio.Sound;
import crossj.hacks.gameio.Sprite;
import crossj.hacks.gameio.Texture;
import crossj.hacks.image.Color;

public final class GamePlaceholder implements Game {
    private GameIO io;
    private Batch batch;
    private Texture texture;
    private Sprite sprite;
    private Music music;
    private Sound sound;

    @Override
    public void init(GameIO io) {
        this.io = io;
        var graphics = io.getGraphics();
        batch = graphics.newBatch();
        texture = graphics.newTextureFromAsset("demo.bmp");
        sprite = texture.newSpriteFromEntireTexture();
        music = io.getAudio().newMusicFromAsset("bach.mp3");
        sound = io.getAudio().newSoundFromAsset("sine1k.wav");

        music.play();
    }

    @Override
    public void render() {
        var graphics = io.getGraphics();
        graphics.clear(Color.GREEN);
        batch.begin();
        batch.draw(sprite, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
        music.dispose();
        sound.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        IO.println("keycode = " + keycode);
        sound.play(0.2);
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        IO.println("x = " + x + ", y = " + y + ", button = " + button + ", pointer = " + pointer);
        if (music.isPlaying()) {
            music.pause();
        } else {
            music.play();
        }
        return true;
    }
}
