package velocity;

import velocity.util.*;

/** 
 * Configure Velocity to how it best suits your game. For game development,
 * subclass this and assign new values to every field in this file to change the defaults.
 * Then, create an instance of that class and pass it into {@code VelocityMain.app_main}
 */
public class GlobalAppConfig {
    /**
     * Currently loaded Velocity configuration. Visible across the Velocity stack 
     * (like VXRA and appcode).
     */
    public static GlobalAppConfig bcfg;

    /*********************** WINDOW CONFIG **************************/
    /** Name shown on the window title bar. */
    public String APP_NAME;

    /** Icon drawn in the window title bar. */
    public String ICON_PATH;

    /** Set the default window resolution on game start. */
    public Point APP_RES_DEFAULT;

    /** Allow dynamic window resizing of the game view and camera. */
    public boolean WINDOW_RESIZABLE;

    /** Start the application in fullscreen (or fake fullscreen depending on the renderer). */
    public boolean WINDOW_FULLSCREEN;

    /********************** RENDERER CONFIG *************************/
    /** 
     * Preferred renderer to use (if available and supported). The name of the renderer. LumaViper
     * and the ERP are supplied by default.
     */
    public String DEFAULT_RENDERER;

    /** One of the supported backends for the provided renderer. Generally CPU, OpenGL, or DirectX11. */
    public String RENDER_BACKEND;

    /** Allow or disable the automatic ERP fallback on failure to load others on the fallback chain. */
    public boolean ENABLE_ERP_FALLBACK;

    /** Warn the user when the default renderer could not initialize. */
    public boolean WARN_RENDERER_INIT_FAIL;

    /** Enable the debug renderer for easier debugging and viewing of objects. */
    public boolean EN_DEBUG_RENDERER;

    /** Worker core count for renderers that use worker threads (like LVCPU). */
    public int REND_WORKER_COUNT;

    /********************* SCENE LOAD CONFIG ************************/
    /** The scene first loaded when Velocity starts. */
    public String START_SCENE;

    /** Scene loaded when the requested scene could not be found. */
    public String LOAD_FAILURE_SCENE;

    /** A failed scene load crashes the game instead of loading the error scene. */
    public boolean SCENE_LOAD_FAILURE_FATAL;

    /******************** WARNINGS AND ERRORS ***********************/
    /** Missing images, instead of returning no image, crash the game. */
    public boolean MISSING_IMAGE_FATAL;

    /** Any warnings are increased in severity to an exception. Useful for debugging. */
    public boolean WARNINGS_FATAL;

    /************************* DEBUGGING ****************************/
    /** Log all GC events (like scene destruction and deletion of sprites.) */
    public boolean LOG_GC;

    /** Enable the memory allocation/deallocation profiler for sprites. */
    public boolean LOG_MEMORY;

    /********************* RENDERER DEBUGGING ***********************/
    /** Enable the swapchain and draw profiling. */
    public boolean EN_RENDERER_LOGS;

    /** Track frame by frame drawtime. */
    public boolean EN_RENDERER_PROFILER;

    /** Track shader time. Not supported on OGL/DX11 */
    public boolean PROFILE_SHADERTIME;

    /************************ CONFIGURATION *************************/
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
        this.DEFAULT_RENDERER = "ERP";  // Velocity will force usage of the ERP generally.
        this.RENDER_BACKEND = "DEFAULT";  // The ERP does not support different backends.
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
