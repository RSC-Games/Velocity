package velocity;

import velocity.util.*;

/** 
 * Configure Velocity to how it best suits your game.
 */
public class GlobalAppConfig {
    /**
     * Velocity configuration. Visible across the Velocity stack (like VXRA and appcode).
     */
    public static GlobalAppConfig bcfg;

    /*********************** WINDOW CONFIG **************************/
    public String APP_NAME;                   // Name shown on the window.
    public String ICON_PATH;                  // Window icon.
    public Point APP_RES_DEFAULT;             // Sets default window resolution.
    public boolean WINDOW_RESIZABLE;          // Allows dynamic rescaling of the window and game.
    public boolean WINDOW_FULLSCREEN;         // Auto-starts the application in fullscreen.

    /********************** RENDERER CONFIG *************************/
    public String DEFAULT_RENDERER;           // Renderer to use when available.
    public String RENDER_BACKEND;             // Supported: CPU, OpenGL, DirectX11 (stubbed).
    public boolean ENABLE_ERP_FALLBACK;       // Allow the default ERP fallback.
    public boolean WARN_RENDERER_INIT_FAIL;   // Warn the user when a renderer cannot initialize.
    public boolean EN_DEBUG_RENDERER;         // Don't ship a build this way.
    public int REND_WORKER_COUNT;             // Default how many CPUs are allowed to be used.

    /********************* SCENE LOAD CONFIG ************************/
    public String START_SCENE;                // Initial scene loaded by Velocity.
    public String LOAD_FAILURE_SCENE;         // Scene loaded when the requested scene can't be found.
    public boolean SCENE_LOAD_FAILURE_FATAL;  // By default failed scene loads will warn.

    /******************** WARNINGS AND ERRORS ***********************/
    public boolean MISSING_IMAGE_FATAL;       // Force missing image warnings to errors.
    public boolean WARNINGS_FATAL;            // Trigger crash handler on any warning.

    /************************ DEBUGGING *****************************/
    public boolean LOG_GC;                    // Enable GC logging messages.
    public boolean LOG_MEMORY;                // Log memory allocation/deallocations for sprites.

    /********************* RENDERER DEBUGGING ***********************/
    public boolean EN_RENDERER_LOGS;          // Enable renderer swapchain and draw messages.
    public boolean EN_RENDERER_PROFILER;      // Track drawtime.
    public boolean PROFILE_SHADERTIME;        // Track shading drawtime. Not supported on OGL/DX11.

    /**
     * Configure this application. Takes a list of supplied config data and
     * starts Velocity and the application with that data.
     * 
     * @apiNote Override this constructor in the game app config so you can alter
     *  these values. These are application defaults.
     */
    public GlobalAppConfig() {
        // Window config.
        this.APP_NAME = "Velocity Application";  // Standard application name.
        this.ICON_PATH = "./velocity/resources/rsc_games.ico";  // Path to the RSC Games logo.
        this.APP_RES_DEFAULT = new Point(640, 480);  // Default application resolution.
        this.WINDOW_RESIZABLE = true;  // Developers are encouraged to allow resizable windows.
        this.WINDOW_FULLSCREEN = false; // By default will be a windowed application.

        // Renderer config.
        this.DEFAULT_RENDERER = "LumaViper";  // Velocity will force usage of the ERP generally.
        this.RENDER_BACKEND = "CPU";  // The ERP does not support different backends.
        this.ENABLE_ERP_FALLBACK = true;  // When no renderers are available use the ERP.
        this.WARN_RENDERER_INIT_FAIL = true;  // Warn the dev when the renderer cannot start.
        this.EN_DEBUG_RENDERER = false;  // The Debug Renderer is a finicky thing and takes time.
        this.REND_WORKER_COUNT = Runtime.getRuntime().availableProcessors() - 1; // Default CPU count.

        // Scene loader config.
        this.START_SCENE = "DefaultScene";  // Velocity will start an internal scene in library.
        this.LOAD_FAILURE_SCENE = "ErrorScene";  // Default scene load failure scene.
        this.SCENE_LOAD_FAILURE_FATAL = true;  // Missing scenes will trigger the crash handler.

        // Generic warnings and error system.
        this.MISSING_IMAGE_FATAL = true;  // Report all images Velocity cannot find.
        this.WARNINGS_FATAL = false;  // Force all warnings to be fatal exceptions.

        // Velocity/application debugging system.
        this.LOG_GC = false;  // By default do not log any GC/Warning messages.
        this.LOG_MEMORY = false;  // Disable the memory profiling system.

        // Renderer debugging/profiling system.
        this.EN_RENDERER_LOGS = false;  // Takes critical rendering time to log.
        this.EN_RENDERER_PROFILER = false;  // Profiles at a deeper level. Disabled.
        this.PROFILE_SHADERTIME = false;  // Do not track shader execution time.
    }
}
