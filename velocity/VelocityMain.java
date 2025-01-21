package velocity;

import velocity.renderer.window.WindowConfig;
import velocity.renderer.window.WindowOption;
import velocity.system.FileResourceLoader;
import velocity.system.JARResourceLoader;
import velocity.system.ResourceLoader;
import velocity.renderer.debug.DebugRenderer;
import velocity.renderer.RendererFeatures;
import velocity.renderer.RenderPipeline;

import java.io.IOException;

import velocity.config.GlobalAppConfig;
import velocity.config.GlobalSceneDefs;
import velocity.renderer.DrawTimer;
import velocity.util.Logger;
import velocity.util.MemTracerUtil;
import velocity.util.Version;
import velocity.util.Popup;

/**
 * Core body of Velocity. Everything that Velocity does originates here.
 * 
 * Updates (Velocity v0.6.5.2)
 *  - (1/20/2025) Implemented modern replacements for the legacy DebugRenderer system.
 *  - (1/20/2025) Reimplemented collision engine so it could handle faster bodies.
 *  - (1/20/2025) Physics engine never actually was simulated. Fixed.
 *  - (1/19/2025) Switched all sprites to use the Transform system and fixed a long standing scene
 *      renderer limitation.
 * 
 * Updates (Velocity v0.6.4.1)
 *  - (1/19/2025) Added a basic physics engine on top of the collision support.
 *  - (1/18/2025) Finally added virtual resolution support (for CopperheadGL).
 * 
 * Updates (Velocity v0.6.3.5)
 *  - (1/18/2025) Added one shot functionality to the legacy animation engine.
 *  - (8/20/2024) Fixed GraalVM crash (due to missing property; unable to start resourceloader.)
 *  - Added elapsed, elapsedms, and reset to velocity.util.Counter.
 *  - Updated the Memory Tracer subsystem.
 *  - Added Persistence to the scene system.
 * 
 * Updates (Velocity v0.6.2.5)
 *  - ERP didn't output tab events.
 *  - Added a query option to the popup system.
 *  - Added in a logger system.
 *  - UIButton now has hover and not hovering events (for animating buttons).
 *  - VXRA now forces the window out of fullscreen when an exception occurs.
 *  - Removed extra velocity.Images helper class.
 *  - Moved GlobalAppConfig and GlobalSceneDefs into velocity.config
 *  - VXRA unstable renderer error message can be suppressed and will be replaced 
 *      with an in-game warning message.
 *  - User can now voluntarily enter/exit fullscreen via window.enterFullScreen/exitFullScreen.
 *  - All VXRA-compliant renderers must now be on the same version as VXRA or they 
 *      will not be loaded.
 *  - Updated VXRA version to v0.6a
 * 
 * Updates (Velocity v0.6.1.2)
 *  - Fixed transient keypresses in the InputSystem (via a Publish mechanism).
 *  - Reached Beta release.
 *  - Fixed the version class... again.
 *  - Updated InputSystem API and introduced getKeyDown() + released()
 *  - Enhanced crash dump system.
 * 
 * Updates (Velocity v0.5.2.7)
 *  - Created the Velocity application exception crash dumper.
 *  - Fixed Scene loader validation rules.
 *  - Window interface expanded to include getPointerLocation().
 *  - Fixed classloader resource leakage in VXRA.
 *  - Improved type enforcement in the Scene instantation code.
 *  - The entirety of the Velocity API has been commented and documented.
 *  - Introducing the ResourceLoader API.
 *  - Velocity.Images now chains exceptions on failed image load.
 * 
 * Updates (As of Velocity v0.5.1.8)
 *  - Version comparison bugs patched (isOlder didn't work).
 *  - Patched a long-standing bug with sprite initialization.
 *  - Made the engine version constant publicly available.
 *  - ERP can now be disabled and VXRA will warn when a renderer cannot be
 *      created.
 *  - Minor lighting system changes.
 *  - Introduced the varied input handler backend. It does add a degree of
 *      complexity to VXRA so it may be changed.
 *      - LVOGL, LVCPU, and the ERP all support it.
 * 
 * Updates (As of Velocity v0.5.0.0)
 *  - VXRA now capable of loading renderer JARs. Note that they must *NOT*
 *      be on the classpath!
 *  - Velocity source is now bundled separately from the application source.
 * 
 * Updates (As of Velocity v0.4.1.4)
 *  - Fullscreen support implemented across the ERP and LVCPU.
 *  - LVCPU Lighting Engine slightly further optimized for bright sunlights.
 *  - LVCPU lighting cache no longer erased every frame - so effectively
 *     sunlights are baked.
 *  - Memory tracing and profiling significantly improved.
 *  - VXRA and the entire Velocity Game-facing Rendering API have been changed.
 *     Consult the javadoc for more info.
 * 
 * Updates (As of Velocity v0.3.6.1)
 *  - Z-sorting in LVCPU and VXRA fully supported.
 *  - LVCPU internally has switched to a job-based rendering system. Multithreading
 *      is currently supported, with Z-sorting coming in the next major update.
 * 
 * Updates (As of Velocity v0.3.5.6)
 *  - LVCPU: More optimizations -> Significantly reduced pixel operations.
 *  - New UITextBox class implemented to simplify drawing text on-screen.
 *  - Working on Point.normalize()
 *  - Sprite allocation tracing and Scene GC memory profiling.
 *  - Leaked allocation reporting (does not give the callstack or object pointers!).
 * 
 * Updates (As of Velocity v0.3.4.5)
 *  - Scene GC memory tracking (not guaranteed to be correct but seems to be accurate).
 *  - Minor VXRA update (changed drawText to use a provided Font object)
 *  - Added Point constants (Point.zero, Point.one)
 *  - LVCPU is now fully VXRA-compliant.
 *  - Replaced AWT in DrawTimer for custom EventHandler.
 *  - Mouse support completed.
 *  - engine.renderer.lumaviper renamed to engine.renderer.lvcpu
 *  - Extensive refactoring.
 *  - engine package renamed to velocity.
 *  - Fixed concurrency issues with collision and tick.
 *  - Implemented triggerable support.
 *  - Minor lighting engine optimizations and fixed alpha copy bugs.
 * 
 * Updates (As of Velocity v0.2.8.2)
 *  - Mouse support implemented. Testing.
 *  - Minor VXRA patches and error handling added.
 *  - New warning to error flags for suppressing and increasing severity.
 *  - Major code refactoring.
 */
public class VelocityMain implements Driver {
    /**
     * Current Velocity version. Uses the semantic versioning system
     * VERSION.MAJOR.MINOR.PATCH.
     */
    public static final Version VELOCITY_VER = new Version(0, 6, 5, 2);

    /**
     * Extensions to the Velocity version.
     * "pa" stands for prealpha release. (PROMOTED, NO LONGER USED).
     * "a" stands for alpha release.
     * "b" stands for beta release.
     * "dev" stands for Development Release.
     * "rc<X>" stands for Release Candidate (X)
     * "p" stands for Production Release.
     */
    public static final String VELOCITY_EXT = "dev";

    /**
     * Initialize and run Velocity.
     * 
     * @param bcfg The application and Velocity configuration parameters.
     * @param sceneDefs The scene definitions for Scene loads.
     */
    public static void app_main(GlobalAppConfig bcfg, GlobalSceneDefs sceneDefs) {
        // Initial velocity engine property provisioning.
        GlobalAppConfig.bcfg = bcfg;
        Scene.sceneLUT = sceneDefs;

        // Initialize the application resource loader.
        String mainFile = System.getProperty("sun.java.command");

        // Assume file resources (probably compiled with GraalVM).
        if (mainFile == null)
            ResourceLoader.registerAppResourceLoader(new FileResourceLoader());

        // Try standard resourceLoader system.
        else {
            try {
                ResourceLoader appLdr = !mainFile.endsWith(".jar") ? new FileResourceLoader() 
                                        : new JARResourceLoader(mainFile);
                ResourceLoader.registerAppResourceLoader(appLdr);
            }
            catch (IOException ie) {
                ie.printStackTrace();
                System.exit(1);
            }
        }

        // Start Velocity.
        Logger.log("main", "Launched Velocity version " + VELOCITY_VER + "-" + VELOCITY_EXT);
        Logger.log("main", "Starting Velocity system...");
        new VelocityMain();  // Start and initialize the base engine code.

        // Start engine tick and rendering.
        Logger.log("main", "Velocity up. Entering player loop.");
        DrawTimer t = VXRA.rp.getTimer();

        // Prevent a full window close and process termination on error, and inform
        // the user. In the event of a fatal error, force the window out of fullscreen.
        try {
            while (true) {
                t.tick();
            }
        }
        // A scene could not be loaded for some reason.
        catch (InvalidSceneException ie) {
            VXRA.rp.getWindow().exitFullScreen();
            Logger.error("main", "Exception in thread main " + 
                               "velocity.InvalidSceneException: " + ie.getMessage());
            CrashHandler.writeCrashInfo(ie, "The provided scene could not be loaded.");
            Popup.showError("Velocity Application Error", 
                            "The application has crashed! Reason:\n" + ie.getMessage());
        }
        // Game crashed for some reason. Tell the user/dev.
        catch (Exception ie) {
            VXRA.rp.getWindow().exitFullScreen();
            Logger.error("main", "Exception in thread main ");
            ie.printStackTrace();
            CrashHandler.writeCrashInfo(ie, "A generic crash has occurred.");
            Popup.showError("Velocity Application Error", 
                            "The application has crashed! Reason:\n" + ie.getMessage());
        }
        // Some generic JVM error. Generally when the VSCode streaming compiler fails. 
        // Tell the user/dev.
        catch (Error ie) {
            VXRA.rp.getWindow().exitFullScreen();
            Logger.error("main", "Exception in thread main ");
            ie.printStackTrace();
            CrashHandler.writeCrashInfo(ie, "Streaming Compiler Error.");
            Popup.showError("Velocity Streaming Compiler Error", 
                            "Part of the application failed to compile! Check log for info.");
        }
    }              

    /**
     * Creates the window context, renderer, and initializes the full Velocity callback
     * system.
     */
    public VelocityMain() {
        // Generate the window configuration according to the config specified in {@code
        // AppConfig}
        WindowConfig windowConfig = new WindowConfig(
            GlobalAppConfig.bcfg.APP_NAME, 
            GlobalAppConfig.bcfg.APP_RES_DEFAULT,
            GlobalAppConfig.bcfg.ICON_PATH
        );

        windowConfig.setOption(WindowOption.HINT_FULLSCREEN, GlobalAppConfig.bcfg.WINDOW_FULLSCREEN);
        windowConfig.setOption(WindowOption.HINT_RESIZABLE, GlobalAppConfig.bcfg.WINDOW_RESIZABLE);
        // The window is hardcoded to disable always on top.

        // Ask VXRA to find us the LumaViper renderer implementation.
        Logger.log("main", "Loading renderer...");
        VXRA.newRenderPipeline(
            GlobalAppConfig.bcfg.DEFAULT_RENDERER, 
            GlobalAppConfig.bcfg.RENDER_BACKEND, 
            windowConfig,
            this
        );

        // Enable scene allocation memory tracing.
        new MemTracerUtil();  // Stored internally in the class.

        // List renderer featureset (for debugging purposes)
        logRendererFeatureSet();

        // Pre-load the first scene (usually the shader loading scene).
        Logger.log("main", "Starting Velocity Scene subsystem.");
        Scene.scheduleSceneLoad(GlobalAppConfig.bcfg.START_SCENE);
        
        // Start the window event system.
        Logger.log("main", "Starting render pipeline.");
        VXRA.rp.init();

        if (GlobalAppConfig.bcfg.EN_DEBUG_RENDERER)
            startDebugRenderer();
    }

    /**
     * Internal. Starts and initializes the debug renderer system. May be removed.
     */
    private void startDebugRenderer() {
        DebugRenderer dr = new DebugRenderer();
        dr.init();
    }

    /**
     * Callback from {@code Driver} and whatever event handler is called when the
     * {@code engine.renderer.DrawTimer} fires. Responsible for game tick and
     * rendering.
     */
    public void gameLoop() {
        // Process any scene load requests that may have come up during the last tick.
        Scene.INTERNAL_runSceneLoads();

        // Game tick
        Scene.currentScene.tick();

        // Run the LumaViper render module.
        VXRA.rp.render();
    }

    /**
     * Assists in debugging and reporting the current renderer. Prints the available
     * featureset of the supplied renderer.
     */
    private void logRendererFeatureSet() {
        RenderPipeline rp = VXRA.rp;
        RendererFeatures featureSet = rp.getFeatureSet();

        Logger.log("main", "VXRA returned renderer (" + rp.getRendererName() + ")");
        Logger.log("main", "Got core feature set: ");
        Logger.log("main", "\t FEAT_required: " + isAvail(featureSet.FEAT_required));
        Logger.log("main", "\t FEAT_doubleBuffered: " + isAvail(featureSet.FEAT_doubleBuffered));
        Logger.log("main", "\t FEAT_extended: " + isAvail(featureSet.FEAT_extended));
        Logger.log("main", "\t FEAT_lighting: " + isAvail(featureSet.FEAT_lighting));
        Logger.log("main", "\t FEAT_shaders: " + isAvail(featureSet.FEAT_shaders));
        Logger.log("main", "\t FEAT_spriteShaders: " + isAvail(featureSet.FEAT_spriteShaders));
        Logger.log("main", "\t FEAT_screenShaders: " + isAvail(featureSet.FEAT_screenShaders));
        Logger.log("main", "\t FEAT_extensions: ...");
        Logger.log("main", " End of renderer feature reporting.");
    }


    /**
     * Internal helper for string formatting.
     */
    private String isAvail(boolean flag) {
        return flag ? "\033[32mAvailable\033[0m" : "\033[31mMISSING\033[0m";
    }
}