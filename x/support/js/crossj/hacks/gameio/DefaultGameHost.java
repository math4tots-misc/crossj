package crossj.hacks.gameio;

public final class DefaultGameHost implements GameHost {
    public static native DefaultGameHost getDefault();

    @Override
    public native void run(Game game);
}
