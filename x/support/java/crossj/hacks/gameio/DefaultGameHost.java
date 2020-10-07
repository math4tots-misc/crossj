package crossj.hacks.gameio;

public final class DefaultGameHost implements GameHost {
    private static final DefaultGameHost INSTANCE = new DefaultGameHost();

    public static DefaultGameHost getDefault() {
        return INSTANCE;
    }

    @Override
    public void run(Game game) {
        // TODO
        // FWIW, DefaultGameHost is not used for libgdx host
    }
}
