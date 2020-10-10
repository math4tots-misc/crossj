package crossj.hacks.gameio;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class GdxBatch implements Batch {
    private final SpriteBatch batch;

    GdxBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void begin() {
        batch.begin();
    }

    @Override
    public void end() {
        batch.end();
    }

    @Override
    public void drawStretched(Sprite sprite, double x, double y, double width, double height) {
        batch.draw(cast(sprite).getSprite(), (float) x, (float) y, (float) width, (float) height);
    }

    private static GdxSprite cast(Sprite sprite) {
        return (GdxSprite) sprite;
    }

    @Override
    public void drawText(BMFont font, String text, double x, double y) {
        ((GdxFont) font).getFont().draw(batch, text, (float) x, (float) y);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
