package crossj.hacks.gameio;

import crossj.base.Bytes;

/**
 * Context used for reading files and accessing bundled assets/resources.
 */
public interface FileSystemContext {

    public String readAsset(String path);

    public Bytes readAssetBytes(String path);
}
