package crossj.hacks.gameio.placeholder;

import crossj.base.IO;
import crossj.hacks.gameio.Batch;
import crossj.hacks.gameio.Game;
import crossj.hacks.gameio.BMFont;
import crossj.hacks.gameio.GameIO;
import crossj.hacks.gameio.InputHandler;
import crossj.hacks.gameio.Key;
import crossj.hacks.gameio.MouseButton;
import crossj.hacks.gameio.Music;
import crossj.hacks.gameio.Sound;
import crossj.hacks.gameio.Sprite;
import crossj.hacks.gameio.Texture;
import crossj.hacks.image.Color;

public final class PlaceholderGame implements Game, InputHandler {
    private GameIO io;
    private Batch batch;
    private Texture texture;
    private Sprite sprite;
    private Music music;
    private Sound sound;
    private String message;
    private BMFont font;
    private Texture blueRectTexture;
    private Sprite blueRect;
    private Texture redRectTexture;
    private Sprite redRect;
    private Texture yellowRectTexture;
    private Sprite yellowRect;

    @Override
    public void init(GameIO io) {
        this.io = io;
        io.getInput().setInputHandler(this);
        var graphics = io.getGraphics();
        batch = graphics.newBatch();
        texture = graphics.newTextureFromAsset("demo.bmp");
        sprite = texture.newSpriteFromEntireTexture();
        music = io.getAudio().newMusicFromAsset("bach.mp3");
        sound = io.getAudio().newSoundFromAsset("sine1k.wav");

        blueRectTexture = graphics.newTextureFromColors(10, 10, (x, y) -> Color.BLUE);
        blueRect = blueRectTexture.newSpriteFromEntireTexture();

        redRectTexture = graphics.newTextureFromColors(1, 1, (x, y) -> Color.RED);
        redRect = redRectTexture.newSpriteFromEntireTexture();

        yellowRectTexture = graphics.newTextureFromColors(1, 1, (x, y) -> Color.YELLOW);
        yellowRect = yellowRectTexture.newSpriteFromEntireTexture();

        var fs = io.getFileSystem();
        message = fs.readAsset("foo.txt");
        IO.println("foot.txt => " + message);

        font = graphics.getDefaultFont();

        music.play();
    }

    @Override
    public void render() {
        var graphics = io.getGraphics();
        var input = io.getInput();
        graphics.clear(Color.GREEN);
        batch.begin();
        batch.draw(sprite, 0, 0);
        batch.drawText(font, message, 50, 50);
        batch.drawText(font, message, io.getGraphics().getWidth() / 2, io.getGraphics().getHeight() / 2);
        batch.drawText(font, "height = " + font.getLineHeight(), 50, io.getGraphics().getHeight() / 2);
        batch.drawText(font, "width = " + font.getWidth(message), 50,
                io.getGraphics().getHeight() / 2 + font.getLineHeight());

        batch.drawStretched(redRect, (double) (io.getGraphics().getWidth() - 100),
                (double) (io.getGraphics().getHeight() - 100), 100, 100);
        batch.draw(blueRect, (double) (io.getGraphics().getWidth() - 100),
                (double) (io.getGraphics().getHeight() - 100));

        var x = input.getX();
        var y = input.getY();
        batch.drawStretched(input.isMouseButtonPressed(MouseButton.LEFT) ? redRect : yellowRect, x - 10, y - 10, 20,
                20);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        music.dispose();
        sound.dispose();
        font.dispose();
        blueRectTexture.dispose();
        redRectTexture.dispose();
        yellowRectTexture.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        IO.println("keycode = " + keycode + " (" + Key.toString(keycode) + ")");
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

    @Override
    public boolean scrolled(int amount) {
        IO.println("Scrolled: " + amount);
        return false;
    }
}
