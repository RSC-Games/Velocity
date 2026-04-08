package com.rsc_games.velocity;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.rsc_games.velocity.VXRA;
import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.RenderPipeline;
import com.rsc_games.velocity.renderer.erp.EmbeddedRenderPipeline;
import com.rsc_games.velocity.renderer.window.WindowConfig;
import com.rsc_games.velocity.util.Logger;
import com.rsc_games.velocity.util.Popup;
import com.rsc_games.velocity.util.Warnings;

/**
 * Velocity's Extensible Renderer Architecture core integration implementation.
 * Uses and allows the VXRA API to be used with generic renderers.
 * 
 * VXRA Version Updates:
 * 
 * VXRA 0.6.3a (4/8/2026):
 *  - VXRA has been deprecated and the recommended render pipeline will now be built into
 *      velocity rather than externally loaded.
 * 
 * VXRA 0.6.2a (4/30/2025): 
 *  - Introduced cleanups to image loading code which should reduce disk bandwidth requirements 
 *      and decrease loading times.
 *  - Future support for multiple camera render targets.
 * 
 * VXRA 0.6.1a:
 *  - Cleaned up old obsolete renderer hooks code and introduced Virtual Resolution support.
 */
@Deprecated(since="0.7.0.0", forRemoval=true)
public class VXRA {
    /**
     * Current provided VXRA version.
     */
    public static final String VXRA_VER = "0.6.3a";

    /**
     * The currently loaded render pipeline.
     */
    public static RenderPipeline rp;

    /**
     * The render pipeline classloader to track.
     */
    private static URLClassLoader rpClassLoader;

    public static RendererDispatcher createRenderer(Driver driver, WindowConfig config) {
        return new RenderThread(driver, config);
    }

    /**
     * Create a new render pipeline with the provided renderer and backend first. 
     * If the requested renderer will not start, VXRA will continue down the fallback
     * chain until a renderer works or no more renderers are available.
     * 
     * @param renderer Name of requested renderer (Velocity default is "LumaViper").
     * @param backend Renderer backend (Velocity default is "CPU")
     * @param cfg Window configuration parameters for the renderer.
     * @param d Driver code {@code Main} that formerly was used by the extension
     *   renderer. May be deprecated.
     * @return The newly created render pipeline.
     */
    public static RenderPipeline newRenderPipeline(String renderer, String backend, 
                                                   WindowConfig cfg, Driver d) {
        
        // TODO: Implement fallback chain eventually.
        Logger.log("vxra", "Velocity eXtensible Renderer Architecture (VXRA " + VXRA_VER + ") loaded.");

        if (rp != null)
            throw new IllegalStateException("Cannot create new render pipeline: a renderer already exists!");

        // Current crappy fallback chain.
        rp = doFallbackChain(renderer, backend, cfg, d);
        warnIfMissing(rp);
        return rp;
    }

    /**
     * Simulate the fallback chain. Try to get the best of the supported renderers.
     * Get the main renderer first and fall back down the chain if the main renderer
     * is not supported on this hardware.
     * 
     * @param renderer The default renderer.
     * @param backend The default backend.
     * @param cfg The window configuration data.
     * @param d The game driver.
     * @return The most suitable render pipeline.
     */
    // TODO: Fallback chain system is badly implemented. Reimplement with an array
    // of renderers in AppConfig.
    private static RenderPipeline doFallbackChain(String renderer, String backend,
                                                  WindowConfig cfg, Driver d) {
        // Initially search for the ERP if requested. The ERP is built into Velocity.
        if (renderer.equals("ERP"))
            return new EmbeddedRenderPipeline(cfg, d);

        // Otherwise load a provided renderer JAR from disk.
        RenderPipeline pipeline;

        //do {
        pipeline = tryLoadRenderer(renderer, backend, cfg, d);
            //if (pipeline == null) 
            //    throw new IllegalStateException("No renderers left to fall back to!");
        //}
        //while (pipeline != null);

        if (pipeline == null) {
            // No renderer, provide the ERP.
            if (GlobalAppConfig.bcfg.ENABLE_ERP_FALLBACK)
                return new EmbeddedRenderPipeline(cfg, d);
            
            // No renderers available; cannot run the game.
            Popup.showError("Velocity Error (VXRA " + VXRA_VER + ")", 
                            "No renderers available!");
        }

        // We found a renderer. Ensure it's compatible with the current Velocity subsystem.
        if (!pipeline.getVXRATargetVersion().equals(VXRA_VER)) {
            // No renderer, provide the ERP.
            if (GlobalAppConfig.bcfg.ENABLE_ERP_FALLBACK)
                return new EmbeddedRenderPipeline(cfg, d);

            // Cannot run the game without a good renderer!
            Popup.showError("Velocity Error (VXRA " + VXRA_VER + ")", 
                            "Found renderer does not support the current VXRA version!");
        }

        return pipeline;
    }

    /**
     * Attempt to load a renderer Jarfile from disk.
     * 
     * @param renderer The current renderer to load.
     * @param backend The current backend to load.
     * @param cfg The window configuration data.
     * @param d The game loop code.
     * @return The instantiated render pipeline.
     */
    private static RenderPipeline tryLoadRenderer(String renderer, String backend, 
                                                  WindowConfig cfg, Driver d) {
        String searchName = renderer + "_" + backend + ".jar";
        Logger.log("vxra", "Searching for renderer " + searchName);

        // Enumerate all renderers currently available.
        File rendererPath = new File("./lib/vxra");

        if (rendererPath.listFiles() == null) {
            Logger.warn("vxra", "No ./lib/vxra directory found. Skipping renderer linkage.");
            return null;
        }

        // NOTE: Why am I searching the directory instead of just directly looking up the file?
        // Fallback chain?
        for (final File rendererF : rendererPath.listFiles()) {
            if (rendererF.isDirectory()) continue;

            // Don't load a non-executable file!
            String name = rendererF.getName();
            if (name.lastIndexOf(".jar") != name.length() - 4)
                continue;

            // Only create a pipeline that matches the requested name.
            if (name.equals(searchName)) {
                URLClassLoader rpLoader = getRendererClassLoader(rendererF);

                boolean forceSC = getRequiresSingleThread(rpLoader);

                // TODO: Redesign renderer instantiation (move it to the dispatcher?)
                RenderPipeline p = instantiatePipeline(rpLoader, cfg, d);

                if (p == null && GlobalAppConfig.bcfg.WARN_RENDERER_INIT_FAIL)
                    Popup.showWarning("Velocity Warning (VXRA " + VXRA_VER + ")", 
                                      "Unable to start renderer " + renderer + " ("
                                       + backend + ")");

                return p;
            }
        }
        return null;
    }

    /**
     * 
     * @param rpLoader
     * @return
     */
    private static boolean getRequiresSingleThread(URLClassLoader rpLoader) {
        try {
            Class<?> cls = rpLoader.loadClass("RPInfo");
            Field forceSingleThreaded = cls.getField("FORCE_SINGLE_THREADED");
            return (Boolean)forceSingleThreaded.get(null);
        }
        catch (ClassNotFoundException ie) {
            ie.printStackTrace();
        } 
        catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } 
        catch (IllegalAccessException ie) {
            ie.printStackTrace();
        } 
        catch (NoSuchFieldException ie) {
            ie.printStackTrace();
        }

        return false;
    }

    /**
     * Load the jarfile containing the render pipeline code.
     * @implNote The provided pipelines must NOT be provided on the CLASSPATH!
     *   Otherwise the loading mechanism breaks!
     * 
     * @param rf The render pipeline jarfile.
     * @return The classloader for this jarfile.
     */
    private static URLClassLoader getRendererClassLoader(File rf) {
        try {
            return new URLClassLoader(
                new URL[] {rf.toURI().toURL()},
                VXRA.class.getClassLoader()
            );
        }
        catch (MalformedURLException ie) {
            ie.printStackTrace();
        }

        return null;
    }

    /**
     * Create a pipeline from a provided JARFile.
     * @implNote The provided pipelines must NOT be provided on the CLASSPATH!
     *   Otherwise the loading mechanism breaks!
     * 
     * @param loader Renderer class loader.
     * @param cfg Window configuration.
     * @param d Driver code.
     * @return The instantiated pipeline, or none if no pipeline.
     */
    private static RenderPipeline instantiatePipeline(URLClassLoader loader, WindowConfig cfg, Driver d) {
        try {
            // Attempt to load the pipeline class. Requires a class name of
            // "<renderer>RP_<backend>_Backend"
            Class<?> cls = loader.loadClass("RPInfo");
            Field frp = cls.getField("PIPELINE_CLASS");
            Class<?> rpClass = (Class<?>)frp.get(null);

            // Attempt to instantiate the renderer.
            Constructor<?> c = rpClass.getConstructor(WindowConfig.class, Driver.class);
            return (RenderPipeline)c.newInstance(cfg, d);
        }
        catch (IllegalAccessException ie) {}
        catch (NoSuchMethodException ie) {
            ie.printStackTrace();
            Warnings.warn("vxra", "Renderer could not be instantiated! (" + loader.getName() + ")");
        }
        catch (InvocationTargetException ie) {
            ie.getCause().printStackTrace();
            Warnings.warn("vxra", "Could not start renderer " + loader.getName());
        }
        catch (NoSuchFieldException ie) {
            Warnings.warn("vxra", "Cannot get access to main renderer class!");
        }
        catch (ClassNotFoundException ie) {
            Warnings.warn("vxra", "Failed to load main renderer class!");
        }
        catch (InstantiationException ie) {
            Warnings.warn("vxra", "Failed to create the main renderer!");
        }

        deInitPipeline();
        return null;
    }

    /**
     * Warn the user if the required featureset is missing. Generally fires if the
     * supplied renderer does not implement the {@code FEAT_required} featureset.
     * 
     * @param rp Newly instantiated render pipeline.
     */
    private static void warnIfMissing(RenderPipeline rp) {
        if (!rp.getFeatureSet().FEAT_required && !GlobalAppConfig.bcfg.SUPPRESS_UNSTABLE_RENDERER_WARNING) {
            Popup.showWarning(
                "Velocity Warning (" + VXRA_VER + ")",
                "Renderer \"" + rp.getRendererName() + 
                "\" does not support required feature set!\nContinue anyway?"
            );
        }
    }

    /**
     * De-initialize the render pipeline.
     */
    public static void deInitPipeline() {
        // Deinit the render pipeline.
        if (rp != null) {
            rp.deinit();
            rp = null;
        }

        // Free the currently loaded resources.
        if (rpClassLoader != null) {
            try { rpClassLoader.close(); }
            catch (IOException ie) {}

            rpClassLoader = null;
        }
    }
}
