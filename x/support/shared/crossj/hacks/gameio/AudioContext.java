package crossj.hacks.gameio;

public interface AudioContext {
    /**
     * Loads some music given a file in the bundled assets
     * @param assetPath
     * @return
     */
    Music newMusicFromAsset(String assetPath);

    /**
     * Loads a sound given a file in the bundled assets
     * @param assetPath
     * @return
     */
    Sound newSoundFromAsset(String assetPath);
}
