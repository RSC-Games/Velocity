package com.rsc_games.velocity.renderer.erp;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.rsc_games.velocity.Scene;
import com.rsc_games.velocity.renderer.RenderPipeline;

import com.rsc_games.velocity.Driver;
import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.RendererFeatures;
import com.rsc_games.velocity.renderer.RendererImage;
import com.rsc_games.velocity.renderer.window.Window;
import com.rsc_games.velocity.renderer.window.WindowConfig;
import com.rsc_games.velocity.sprite.Camera;
import com.rsc_games.velocity.util.Counter;
import com.rsc_games.velocity.util.Logger;
import com.rsc_games.velocity.util.Timer;

/**
 * The embedded render pipeline is a bare-bones render pipeline that lacks
 * a lighting engine. It supports VXRA's required featureset only. Double-
 * buffering is not supported. It's provided as an extremely low-performance
 * cost alternative to the feature-rich LumaViper Rendering Pipeline.
 */
@Deprecated(since="0.7.0.0", forRemoval=true)
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
        this.window = new ERPWindow(windowCfg, this);

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
        //this.window.startEventTimer(17); // 17 ms
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

        // Process window events.
        window.erpEvent.postRenderHooks();

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
        return "0.6.2a";
    }

    /**
     * Get the window's handle.
     */
    @Override
    public Window getWindow() {
        return this.window;
    }

    /**
     * Internal function. Queries the renderer cache for the texture associated with
     * the provided file path. Ideally used to reduce disk I/O time.
     * 
     * @param path Texture path.
     * @return Whether the provided texture has already been loaded.
     */
    @Override
    public boolean isTextureCached(String path) {
        return this.texContextMgr.isTextureLoaded(path);
    }

    /**
     * Give the texture manager an image to load.
     * 
     * @param image The image bytes to intern if necessary.
     * @param path The image loading path.
     * @return Whether the image was successfully loaded.
     */
    @Override
    public boolean registerTexture(BufferedImage image, String path) {
        return this.texContextMgr.INTERNAL_loadNewImage(image, path) != null;
    }

    /**
     * Get the texture handle from the provided image path. This function should only
     * fetch from the render cache.
     * 
     * @param path The image path.
     * @return The image handle.
     */
    @Override
    public RendererImage getTextureHandleFromPath(String path) {
        return this.texContextMgr.lookupTextureByPath(path);
    } 

    /**
     * Force a garbage collector run in the texture manager. Generally
     * run during a scene load event.
     */
    @Override
    public void forceGCRun() {
        this.texContextMgr.gcRun();
    }

    /**
     * Deinitialize the pipeline. Free all resources, release hardware, etc.
     */
    @Override
    public void deinit() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deinit'");
    }

    /**
     * Stubbed; the ERP has no use for the renderIdle
     */
    @Override
    public void renderIdle() {}
}
