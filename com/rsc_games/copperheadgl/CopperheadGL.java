package com.rsc_games.copperheadgl;

import java.util.HashMap;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.rsc_games.velocity.Scene;
import com.rsc_games.velocity.renderer.RenderPipeline;

import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.RendererFeatures;
import com.rsc_games.velocity.renderer.RendererImage;
import com.rsc_games.velocity.renderer.window.Window;
import com.rsc_games.velocity.renderer.window.WindowConfig;
import com.rsc_games.velocity.renderer.window.WindowOption;
import com.rsc_games.velocity.util.Point;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.awt.image.BufferedImage;

/**
 * CopperheadGL was forked from LVOGL and is a reimplementation of most of the render
 * pipeline.
 */
public class CopperheadGL extends RenderPipeline {
    static final String COPPERHEADGL_VERSION = "0.0.2-dev";

    GLWindow window;
    GLRendererContext iRendererContext;
    GLEventHandler frameEventHandler;
    GLFrameBuffer backBuffer;
    boolean shouldBeFullscreen = false;

    // TODO: Load the required JAR files here.
    public CopperheadGL(WindowConfig cfg) {
        super(new GLLightingEngine());

        ((GLLightingEngine)this.le).setiRP(this);

        //System.setProperty("org.lwjgl.util.Debug", "true");
        //System.setProperty("org.lwjgl.util.DebugLoader", "true");
        this.shouldBeFullscreen = cfg.getOption(WindowOption.HINT_FULLSCREEN);
        this.window = new GLWindow(cfg, this);

        // LVOGL's full renderer feature set has not been implemented.
        this.featureSet = new RendererFeatures(
            false,  // Renderer base features mostly unimplemented.
            true,  // Double-buffering is implemented in GLFW.
            true,  // Lighting Engine tested and working.
            true,  // Extended featureset fully implemented
            false,  // Sprite fragment shading is not supported.
            false,  // Full screen shading is not implemented.
            false,  // No shading support exists.
            new HashMap<String, String>()  // No additional features.
        );

        // Renderer pre-init complete.
        System.out.println("[copper] Started Copperhead Rendering Engine. Copyright 2025 RSC Games. All Rights Reserved.");
        System.out.println("[copper]: Renderer backend: OpenGL 3.3 Core");
        System.out.println("[copper]: Running renderer engine CopperheadGL (version " + COPPERHEADGL_VERSION + ")");
    }

    /**
     * Initialize the window and GLFW. Should already have access to the GPU.
     */
    public void init() {
        GL.createCapabilities();

        // Initialize the GPU renderer context.
        Point wres = this.window.getResolution();
        iRendererContext = new GLRendererContext(wres.x, wres.y, this);
        frameEventHandler = new GLEventHandler(window);
        this.iRendererContext.init();
        this.window.setWindowEventHandler(frameEventHandler);

        // Set a fullscreen window.
        if (this.shouldBeFullscreen)
            this.window.enterFullScreen();

        this.window.setVisible(true);
        this.backBuffer = iRendererContext.getBackBuffer();

        System.out.println("[copper]: Identified GPU: " + GL11.glGetString(GL11.GL_RENDERER));
        System.out.println("[copper]: Hardware vendor: " + GL11.glGetString(GL11.GL_VENDOR));
        System.out.println("[copper]: Driver: " + GL11.glGetString(GL11.GL_VERSION));
    }

    /**
     * Render the game.
     */
    public void render() {
        // Poll key events.
        glfwPollEvents();

        // Ensure the frame should remain open.
        // TODO: Add Application.quit() function to execute Velocity shutdown handlers.
        if (glfwWindowShouldClose(window.getHwnd())) System.exit(0);

        iRendererContext.clearBuffers();

        // Submit all the drawcalls to the backend renderer.
        GLFrameBuffer uiFrameBuffer = iRendererContext.getBackBuffer();
        Scene.currentScene.render(backBuffer, uiFrameBuffer);

        // Modern replacement for the debug renderer.
        // Draw collision rects and other debug information.
        if (GlobalAppConfig.bcfg.EN_DEBUG_RENDERER)
            Scene.currentScene.DEBUG_render(uiFrameBuffer, Scene.currentScene.getCamera().transform.location.getDrawLoc(), new float[] {1f, 1f});

        // Backbuffer has already submitted all of its data to the backend renderer.
        // Use that data to composite a frame.
        iRendererContext.renderFrame();

        // Swap the framebuffers.
        glfwSwapBuffers(window.getHwnd());
    }

    /**
     * Generates a renderer name string. Useful for identifying the current
     * available renderer and backend.
     * 
     * @return Renderer name
     */
    public String getRendererName() {
        return "CopperheadGL";
    }

    /**
     * Returns the renderer feature set. Useful for identifying compatibility issues and performance
     * optimizations.
     * 
     * @return Renderer feature set.
     */
    public RendererFeatures getFeatureSet() {
        return this.featureSet;
    }

    /**
     * Returns the current window registered for painting. Allows getting dimension information
     * and other crucial data.
     * 
     * @return The renderer window.
     */
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
        return this.iRendererContext.isTextureLoaded(path);
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
        return this.iRendererContext.loadImage(image, path) != null;
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
        return this.iRendererContext.lookupTexture(path);
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
     * By default in LumaViper CPU the texture GC only runs once you have 256 textures 
     * loaded in texture memory. This behavior may vary across rendering backends, but
     * in any case, if there are a bunch of unreferenced textures eating up memory, this
     * will force the texture cache to erase them. Then it's up to the JVM GC to clear it.
     */
    public void forceGCRun() {
        iRendererContext.runTextureGC();
    }
}
