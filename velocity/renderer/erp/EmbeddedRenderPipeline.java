package velocity.renderer.erp;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import velocity.Driver;
import velocity.Scene;
import velocity.config.GlobalAppConfig;
import velocity.renderer.DrawTimer;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RenderPipeline;
import velocity.renderer.RendererFeatures;
import velocity.renderer.window.Window;
import velocity.renderer.window.WindowConfig;
import velocity.sprite.Camera;
import velocity.util.Counter;
import velocity.util.Logger;
import velocity.util.Timer;

/**
 * The embedded render pipeline is a bare-bones render pipeline that lacks
 * a lighting engine. It supports VXRA's required featureset only. Double-
 * buffering is not supported. It's provided as an extremely low-performance
 * cost alternative to the feature-rich LumaViper Rendering Pipeline.
 */
public class EmbeddedRenderPipeline extends RenderPipeline {
    /**
     * ERP's window interface.
     */
    ERPWindow window;
    
    /**
     * Texture deduplicator and manager.
     */
    ERPTextureContextManager texContextMgr;

    /**
     * Main framebuffer.
     */
    ERPFrameBuffer fb;

    /**
     * Main ui framebuffer.
     */
    ERPFrameBuffer uifb;

    /**
     * Track the current rendering state for thread sync.
     */
    private volatile boolean rendered = false;

    /**
     * Create and start the embedded render pipeline.
     * 
     * @param windowCfg window system parameters.
     * @param m Driver class (passed by VXRA).
     */
    public EmbeddedRenderPipeline(WindowConfig windowCfg, Driver m) {
        super(m, null);

        // Open the window frame.
        this.window = new ERPWindow(windowCfg, this, m);

        // LVCPU's base renderer setup doesn't mask the GPU for features, so it has
        // a relatively constant feature set.
        this.featureSet = new RendererFeatures(
            true,  // No features implemented
            false,
            false,
            false,
            false,
            false,
            false,
            new HashMap<String, String>()  // No additional features.
        );

        Logger.log("erp","Starting the Velocity Embedded Render Pipeline (ERP)");
        Logger.warn("erp", "Lighting features unsupported!");
        this.regenFrameBuffers();
    }

    /**
     * Initialize this render pipeline, start the draw tick, and init the event
     * handlers.
     */
    @Override
    public void init() {
        // Update camera resolution as the frame may not be the exact
        // requested size.
        // TODO: WARNING! Currently is inflexible and not containerized!
        Camera.res = this.window.getResolution();

        // Start the memory context manager essential for texture deduplication
        // and texture lookup speed.
        this.texContextMgr = new ERPTextureContextManager();

        // Start frame callback handler.
        this.window.setVisible(true);
        this.window.startEventTimer(17); // 17 ms
    }

    @Override
    public void render() {
        Counter c = new Counter();
        
        // Wait for draw thread to swap buffers.
        if (GlobalAppConfig.bcfg.EN_RENDERER_PROFILER)
            c.tick();
        
        // Workaround: Java locks thread access to instance variables during atomic read/write.
        // If we're checking this every VM cycle, the draw thread update will never be seen.
        Timer t = new Timer(5L, 1000, true); // Recurring timer with 5 us between fires
        while (true) {
            if (t.tick() && !this.rendered) break;
        }
        
        if (GlobalAppConfig.bcfg.EN_RENDERER_PROFILER)
            Logger.log("erp", "Spent " + c.tick() + " ns waiting for event thread.");
        
        // Prepare for rendering.
        this.fb.clear();
        this.uifb.clear();

        // Draw the scene and execute drawcalls.
        Scene.currentScene.render(fb, uifb);
        this.rendered = true;

        if (GlobalAppConfig.bcfg.EN_RENDERER_PROFILER) {
            long time = c.tick();
            Logger.log("erp", "Debug: Render time was " + time + " ns ("
                               + (time / 1000000) + " ms)");
        }
    }

    /**
     * Allow the window to access the current render framebuffers.
     * 
     * @return Render framebuffers (index 0: fb, index 1: uifb)
     */
    public ERPFrameBuffer[] getDrawBuffers() {
        return new ERPFrameBuffer[] {this.fb, this.uifb};
    }

    /**
     * Non VXRA compliant function. Called on window resizing to update the
     * render buffers.
     */
    public void regenFrameBuffers() {
        this.fb = new ERPFrameBuffer(Camera.res.x, Camera.res.y);
        this.uifb = new ERPFrameBuffer(Camera.res.x, Camera.res.y, true);
    }

    /**
     * Facilitate the next render cycle after the buffers have been swapped.
     */
    public void clearRenderFlag() {
        this.rendered = false;
    }

    /**
     * Non-VXRA function! Get the draw event timer.
     */
    @Override
    public DrawTimer getTimer() {
        return this.window.getTimer();        
    }

    /**
     * Get this renderer's name.
     * 
     * @return Internal renderer name.
     */
    @Override
    public String getRendererName() {
        return "Embedded Render Pipeline";
    }

    /**
     * Get the VXRA compliance version. Must match the Velocity VXRA version.
     * 
     * @return VXRA compliance version.
     */
    @Override
    public String getVXRATargetVersion() {
        return "0.6a";
    }

    /**
     * Get the window's handle.
     */
    @Override
    public Window getWindow() {
        return this.window;
    }

    /**
     * Generate a new framebuffer for use outside of the renderer.
     * @deprecated This function is a relic from early Velocity and is marked for removal.
     */
    @Override
    public FrameBuffer newFrameBuffer() {
        throw new UnsupportedOperationException("Unimplemented method 'newFrameBuffer'");
    }

    /**
     * Generate a new framebuffer for use outside of the renderer.
     * @deprecated This function is a relic from early Velocity and is marked for removal.
     */
    @Override
    public FrameBuffer newFrameBuffer(int x, int y) {
        throw new UnsupportedOperationException("Unimplemented method 'newFrameBuffer'");
    }

    /**
     * Force the texture manager to load an image.
     * 
     * @param b The image bytes to intern if necessary.
     * @param path The image loading path.
     * @return The loaded image and a handle.
     */
    @Override
    public ERPRendererImage INTERNAL_loadImage(BufferedImage b, String path) {
        return this.texContextMgr.INTERNAL_loadNewImage(b, path);
    }

    /**
     * Force a garbage collector run in the texture manager. Generally
     * run during a scene load event.
     */
    @Override
    public void forceGCRun() {
        this.texContextMgr.gcRun();
    } 
}
