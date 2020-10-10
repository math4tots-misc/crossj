package crossj.hacks.gameio;

import crossj.base.Disposable;

public interface BMFont extends Disposable {
    /**
     * Gets the distance from one line of text to the next
     */
    public double getLineHeight();

    /**
     * Gets the width of the given text if it were to be rendered with this font.
     *
     * NOTE: this is potentially an expensive operation as the string will have to
     * be fully laid out.
     */
    public double getWidth(String text);
}
