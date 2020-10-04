package crossj.hacks.gameio;

import crossj.hacks.image.Color;

public interface Brush {
    void setColor(Color color);
    void fillRect(int x, int y, int width, int height);
}
