package crossj.books.dragon.ch03;

/**
 * Information about the alphabet that these automatas recognize
 *
 * Basically we just have 128 ASCII characters + 1 unicode catch-all
 */
public final class Alphabet {
    private Alphabet() {}

    public static final int COUNT = 129;
    public static final int UNICODE_CATCH_ALL = 128;
}
