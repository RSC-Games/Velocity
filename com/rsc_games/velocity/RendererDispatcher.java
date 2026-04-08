package com.rsc_games.velocity;

/**
 * Render thread. Basic speed optimization and allows rendering and the game tick to run 
 * independently.
 */
abstract class RendererDispatcher {
    /**
     * Main script. Required for basic 
     */
    Driver driver;

    /**
     * Supply the driver (legacy deprecated parameter) required for 
     * VXRA renderer creation.
     * 
     * @param driver The window driver (this file).
     */
    public RendererDispatcher(Driver driver) {
        this.driver = driver;
    }

    /**
     * External control. Switches from pipeline construction to initialization.
     */
    public abstract void initPipeline();

    /**
     * External control. Starts the render loop phase of the dedicated render
     * thread.
     */
    public abstract void startRenderLoop();

    /**
     * Synchronize the engine thread with the render thread, then prepare the next frame.
     * The render thread will automatically dispatch the next frame after this is called.
     * 
     * Only purpose of this function is to prevent the two threads from desyncing and
     * causing some hard to find bugs.
     */
    public abstract void syncWithRenderThread();

    /**
     * Create the initial render pipeline. Only performs basic initialization.
     * 
     * @param driver Main window class (use deprecated).
     */
    private void createPipeline(Driver driver) {
        // // Wait for engine sync before starting the render loop.
        // Timer t = new Timer(5L, 1000, true); // 5 us
        // this.pipelineCreateDone = true;

        // while (true)
        //     if (t.tick() && this.initPipeline) break;
    }

    /**
     * Initialize the pipeline (on the render thread).
     */
    private void tryInitPipeline() {
        // VXRA.rp.init();

        // // Wait for engine sync before starting the render loop.
        // Timer t = new Timer(5L, 1000, true); // 5 us
        // this.initDone = true;

        // // Wait for engine sync before starting the render loop.
        // while (true)
        //     if (t.tick() && this.runRenderLoop) break;
    }
}