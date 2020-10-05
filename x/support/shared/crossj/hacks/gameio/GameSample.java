package crossj.hacks.gameio;

import crossj.base.IO;
import crossj.hacks.image.Color;

public final class GameSample implements Game {
    private GameIO io;
    private int width, height;

    public static void main(String[] args) {
        GameHost.getDefault().run(new GameSample());
    }

    @Override
    public void init(GameIO io) {
        this.io = io;
    }

    @Override
    public void resize(int width, int height) {
        io.requestDraw();
        IO.println("resize " + width + " " + height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void click(int button, int x, int y) {
        var kind = "unknown";
        switch (button) {
            case MouseButton.LEFT: kind = "left"; break;
            case MouseButton.MIDDLE: kind = "middle"; break;
            case MouseButton.RIGHT: kind = "right"; break;
        }
        IO.println("Click " + kind + " x = " + x + " y = " + y);
    }

    @Override
    public void keydown(String key) {
        IO.println("keydown " + key);
        if (key.equals("Escape")) {
            io.requestExit();
        }
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(Brush brush) {
        IO.println("draw " + brush);
        brush.setColor(Color.BLACK);
        brush.fillRect(0, 0, width, height);
        brush.setColor(Color.RED);
        brush.fillRect(0, 0, width / 2, height / 2);
    }
}
