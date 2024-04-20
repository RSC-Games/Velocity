package velocity.system;

import java.io.IOException;

/**
 * Allow explicit resource loading from the Velocity bundle.
 */
public class SystemResourceLoader {
    /**
     * The system resource loader (from Velocity local resources).
     */
    private static ResourceLoader sysLdr;

    static {
        init();
    }

    /**
     * Create a resource loader for the Velocity jarfile.
     */
    private static void init() {
        try {
            sysLdr = new JARResourceLoader("./lib/velocity.jar");
        }
        catch (IOException ie) {
            System.err.println("[sysldr]: Could not create velocity system resource loader!");
            throw new RuntimeException("Failed to create resource loader!");
        }
    }

    /**
     * Get the Velocity system resource loader. Can only be used by the Velocity internals.
     * 
     * @return The ResourceLoader.
     * @throws SecurityException If a non-velocity class tries to access this.
     */
    public static ResourceLoader getSystemResourceLoader() {
        // Permission checks here... eventually.
        return sysLdr;
    }
}
