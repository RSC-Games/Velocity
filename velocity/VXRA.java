package velocity;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.net.URLClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import velocity.renderer.*;
import velocity.renderer.erp.EmbeddedRenderPipeline;
import velocity.renderer.window.WindowConfig;
import velocity.util.Popup;
import velocity.util.Warnings;

/**
 * Velocity's Extensible Renderer Architecture core integration implementation.
 * Uses and allows the VXRA API to be used with generic renderers.
 */
public class VXRA {
    public static final String VXRA_VER = "0.5a";
    public static RenderPipeline rp;

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
     */
    public static RenderPipeline newRenderPipeline(String renderer, String backend, 
                                                   WindowConfig cfg, Driver d) {
        
        // Implement fallback chain eventually.
        System.out.println("[vxra]: Velocity eXtensible Renderer Architecture (VXRA "
                           + VXRA_VER + ") found.");

        if (rp != null)
            throw new IllegalStateException("Cannot create new render pipeline: a renderer already exists!");

        // Current crappy fallback chain.
        rp = doFallbackChain(renderer, backend, cfg, d);
        warnIfMissing(rp);
        return rp;
    }

    private static RenderPipeline doFallbackChain(String renderer, String backend,
                                                  WindowConfig cfg, Driver d) {
        // Initially search for the ERP if requested. The ERP is built into Velocity.
        if (renderer.equals("ERP")) {
            return new EmbeddedRenderPipeline(cfg, d);
        }

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

        return pipeline;
    }

    /**
     * Attempt to load a renderer Jarfile from disk.
     * 
     * @param renderer
     * @param backend
     * @param cfg
     * @param d
     * @return The instantiated render pipeline.
     */
    private static RenderPipeline tryLoadRenderer(String renderer, String backend, 
                                                  WindowConfig cfg, Driver d) {
        String searchName = renderer + "_" + backend + ".jar";
        System.out.println("[vxra]: Searching for renderer " + searchName);

        // Enumerate all renderers currently available.
        File rendererPath = new File("./lib/vxra");

        if (rendererPath.listFiles() == null) {
            System.out.println("[vxra]: No ./lib/vxra directory found. Skipping renderer linkage.");
            return null;
        }

        for (final File rendererF : rendererPath.listFiles()) {
            if (rendererF.isDirectory()) continue;

            // Don't load a non-executable file!
            String name = rendererF.getName();
            if (name.lastIndexOf(".jar") != name.length() - 4)
                continue;

            // Only create a pipeline that matches the requested name.
            if (name.equals(searchName)) {
                RenderPipeline p = instantiatePipeline(rendererF, cfg, d);

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
     * Create a pipeline from a provided JARFile.
     * @implNote The provided pipelines must NOT be provided on the CLASSPATH!
     *   Otherwise the loading mechanism breaks!
     * 
     * @param rf Render Pipeline file.
     * @param cfg Window configuration.
     * @param d Driver code.
     * @return The instantiated pipeline, or none if no pipeline.
     */
    private static RenderPipeline instantiatePipeline(File rf, WindowConfig cfg, Driver d) {
        try {
            URLClassLoader rendererLoader = new URLClassLoader(
                new URL[] {rf.toURI().toURL()},
                VXRA.class.getClassLoader()
            );

            // Attempt to load the pipeline class. Requires a class name of
            // "<renderer>RP_<backend>_Backend"
            Class<?> cls = rendererLoader.loadClass("RPInfo");
            Field frp = cls.getField("PIPELINE_CLASS");
            Class<?> rpClass = (Class<?>)frp.get(null);

            // Attempt to instantiate the renderer.
            Constructor<?> c = rpClass.getConstructor(WindowConfig.class, Driver.class);
            return (RenderPipeline)c.newInstance(cfg, d);
        }
        catch (MalformedURLException ie) {
            ie.printStackTrace();
        }
        catch (IllegalAccessException ie) {}
        catch (NoSuchMethodException ie) {
            ie.printStackTrace();
            Warnings.warn("vxra", "Renderer could not be instantiated! (" + rf.getName() + ")");
        }
        catch (InvocationTargetException ie) {
            ie.getCause().printStackTrace();
            Warnings.warn("vxra", "Could not start renderer " + rf.getName());
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

        return null;
    }

    /**
     * Warn the user if the required featureset is missing. Generally fires if the
     * supplied renderer does not implement the {@code FEAT_required} featureset.
     * 
     * @param rp Newly instantiated render pipeline.
     */
    private static void warnIfMissing(RenderPipeline rp) {
        if (!rp.getFeatureSet().FEAT_required) {
            Popup.showWarning(
                "Velocity Warning (" + VXRA_VER + ")",
                "Renderer \"" + rp.getRendererName() + 
                "\" does not support required feature set!\nContinue anyway?"
            );
        }
    }
}
