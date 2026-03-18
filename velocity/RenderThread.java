package velocity;

import velocity.config.GlobalAppConfig;
import velocity.renderer.window.WindowConfig;
import velocity.renderer.window.WindowOption;
import velocity.util.Logger;
import velocity.util.Timer;

/**
 * Render thread. Basic speed optimization and allows rendering and the game tick to run 
 * independently.
 */
// TODO: Extract into clean API
class RenderThread extends RendererDispatcher implements Runnable {
    private volatile boolean pipelineCreateDone = false;
    private volatile boolean initPipeline = false;
    private volatile boolean initDone = false;
    private volatile boolean runRenderLoop = false;
    private volatile boolean threadWaiting = true;

    private WindowConfig windowConfig;

    /**
     * Supply the driver (legacy deprecated parameter) required for 
     * VXRA renderer creation.
     * 
     * @param driver The window driver (this file).
     */
    public RenderThread(Driver driver, WindowConfig windowConfig) {
        super(driver);
        this.windowConfig = windowConfig;
    }

    /**
     * External control. Switches from pipeline construction to initialization.
     */
    public void initPipeline() {
        Timer t = new Timer(5L, 1000, true); // Recurring timer with 5 us between fires
        while (true)
            if (t.tick() && this.pipelineCreateDone) break;  // Wait until pipeline creation is done.

        this.initPipeline = true;
    }

    /**
     * External control. Starts the render loop phase of the dedicated render
     * thread.
     */
    public void startRenderLoop() {
        Timer t = new Timer(5L, 1000, true); // Recurring timer with 5 us between fires
        while (true)
            if (t.tick() && this.initDone) break;  // Wait until the pipeline init is finished.

        this.runRenderLoop = true;
    }

    /**
     * Synchronize the engine thread with the render thread, then prepare the next frame.
     * The render thread will automatically dispatch the next frame after this is called.
     * 
     * Only purpose of this function is to prevent the two threads from desyncing and
     * causing some hard to find bugs.
     */
    public void syncWithRenderThread() {
        Timer t = new Timer(5L, 1000, true); // Recurring timer with 5 us between fires
        while (true)
            if (t.tick() && this.threadWaiting) break;  // Busy wait until the render thread is idle.

        threadWaiting = false;
    }

    /** 
     * Render thread.
     */
    public void run() {
        createPipeline(driver, windowConfig);
        tryInitPipeline();

        // Unique timer per thread.
        Timer t = new Timer(5L, 1000, true); // 5 us

        // Render loop.
        while (true) {
            while (true) {
                VXRA.rp.renderIdle();
                if (t.tick() && !this.threadWaiting) break;  // Busy wait until the main thread has signalled to keep going.
            }

            VXRA.rp.render();
            threadWaiting = true;
        }
    }

    /**
     * Create the initial render pipeline. Only performs basic initialization.
     * 
     * @param driver Main window class (use deprecated).
     */
    private void createPipeline(Driver driver, WindowConfig windowConfig) {
        Logger.log("main", "Loading renderer...");
        VXRA.newRenderPipeline(
            GlobalAppConfig.bcfg.DEFAULT_RENDERER, 
            GlobalAppConfig.bcfg.RENDER_BACKEND, 
            windowConfig,
            driver
        );

        // Wait for engine sync before starting the render loop.
        Timer t = new Timer(5L, 1000, true); // 5 us
        this.pipelineCreateDone = true;

        while (true)
            if (t.tick() && this.initPipeline) break;
    }

    /**
     * Initialize the pipeline (on the render thread).
     */
    private void tryInitPipeline() {
        VXRA.rp.init();

        // Wait for engine sync before starting the render loop.
        Timer t = new Timer(5L, 1000, true); // 5 us
        this.initDone = true;

        // Wait for engine sync before starting the render loop.
        while (true)
            if (t.tick() && this.runRenderLoop) break;
    }
}