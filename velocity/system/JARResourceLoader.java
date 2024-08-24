package velocity.system;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import velocity.util.Logger;

/**
 * The deployment resource loader. Loads provided resources from a local 
 * JAR file.
 */
public class JARResourceLoader extends ResourceLoader {
    /**
     * Resource bundle path on disk.
     */
    String resPath;

    /**
     * The resource bundle handle.
     */
    JarFile bundle;

    /**
     * Creates a resource loader.
     * 
     * @param resourcePath The path of the resource bundle.
     * @throws IOException If the file cannot be opened.
     */
    public JARResourceLoader(String resourcePath) throws IOException {
        Logger.log("velocity.system", "Creating resource loader with path " + resourcePath);
        this.resPath = resourcePath;
        this.bundle = new JarFile(resourcePath);
    }

    /**
     * Loads an input stream from a provided file path within a jar file.
     * 
     * @param filePath Path within the jar to load from.
     * @return The loaded input stream.
     * @throws IOException If there is no file or it cannot be loaded.
     */
    @Override
    public BufferedInputStream load(String filePath) throws IOException {
        // Parse off the ./ for relative paths.
        if (filePath.substring(0, 2).equals("./"))
            filePath = filePath.substring(2);

        ZipEntry entry = bundle.getEntry(filePath);

        if (entry == null) 
            throw new IOException("Could not load " + filePath + " from ldr " + resPath);

        return new BufferedInputStream(bundle.getInputStream(entry));
    }
}
