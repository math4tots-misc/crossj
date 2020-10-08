package crossj.hacks.gameio;

import crossj.base.Disposable;

public interface Music extends Disposable {
    public void play();
    public void stop();
    public void pause();
    public void setLooping(boolean looping);
    public void setVolume(double volume);
    public boolean isPlaying();
    public boolean isLooping();

    default boolean isStopped() {
        return !isPlaying();
    }
}
