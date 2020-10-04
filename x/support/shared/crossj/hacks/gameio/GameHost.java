package crossj.hacks.gameio;

/**
 * Logic that knows how to run a Game.
 */
public interface GameHost {
    public static GameHost getDefault() {
        return DefaultGameHost.getDefault();
    }

    void run(Game game);
}
