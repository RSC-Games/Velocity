package velocity.system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Load resources from the local filesystem. The default resource loader used
 * when testing a game that isn't ready for deployment.
 */
public class FileResourceLoader extends ResourceLoader {
    /**
     * Create a resourceloader capable of loading files from the local filesystem.
     * 
     * @param filePath Path of the file to load.
     * @return The current file stream.
     * @throws IOException Unspecified IO error.
     */
    public BufferedInputStream load(String filePath) throws IOException {
        File f = new File(filePath);
        return new BufferedInputStream(new FileInputStream(f));
    }
}
