package crossj.hacks.gameio;

import crossj.base.IO;
import crossj.hacks.image.Color;

public final class GameSample implements Game {
    private int width, height;
    private boolean dirty = true;

    public static void main(String[] args) {
        GameHost.getDefault().run(new GameSample());
    }

    @Override
    public void resize(int width, int height) {
        dirty = true;
        IO.println("resize " + width + " " + height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void click(int button, int x, int y) {
        var kind = "unknown";
        switch (button) {
            case MouseButton.LEFT: kind = "left"; break;
            case MouseButton.MIDDLE: kind = "left"; break;
            case MouseButton.RIGHT: kind = "right"; break;
        }
        IO.println("Click " + kind + " x = " + x + " y = " + y);
    }

    @Override
    public void keydown(String key) {
        IO.println("keydown " + key);
    }

    @Override
    public int update(double dt) {
        return dirty ? Game.STATUS_DRAW : 0;
    }

    @Override
    public void draw(Brush brush) {
        dirty = false;
        IO.println("draw " + brush);
        brush.setColor(Color.BLACK);
        brush.fillRect(0, 0, width, height);
        brush.setColor(Color.RED);
        brush.fillRect(0, 0, width / 2, height / 2);
    }
}
