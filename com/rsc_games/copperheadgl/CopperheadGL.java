package com.rsc_games.copperheadgl;

import java.util.HashMap;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.rsc_games.velocity.Scene;
import com.rsc_games.velocity.renderer.RenderPipeline;

import com.rsc_games.velocity.Driver;
import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.FrameBuffer;
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
    public CopperheadGL(WindowConfig cfg, Driver m) {
        super(m, new GLLightingEngine());

        ((GLLightingEngine)this.le).setiRP(this);

        //System.setProperty("org.lwjgl.util.Debug", "true");
        //System.setProperty("org.lwjgl.util.DebugLoader", "true");
        this.shouldBeFullscreen = cfg.getOption(WindowOption.HINT_FULLSCREEN);
        this.window = new GLWindow(cfg, this, m);

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
        frameEventHandler = new GLEventHandler(window, m);
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
     * Return the version of VXRA this renderer was designed to work with.
     * 
     * @return The target VXRA version.
     * @since Velocity v0.6.4.0, VXRA v0.6.1a
     */
    @Override
    public String getVXRATargetVersion() {
        return "0.6.1a";
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
     * Create a new framebuffer for use in the renderer. Mostly used internally but occasionally
     * used in other parts of Velocity.
     * 
     * @return New zeroed framebuffer.
     */
    public FrameBuffer newFrameBuffer() {
        throw new UnsupportedOperationException("Illegal operation; cannot create a new fb!");
    }

    /**
     * Create a new framebuffer of variable size for use in the renderer. Mostly used 
     * internally but occasionally used in other parts of Velocity.
     * 
     * @param x Framebuffer width
     * @param y Framebuffer height
     * @return New zeroed framebuffer.
     */
    public FrameBuffer newFrameBuffer(int x, int y) {
        throw new UnsupportedOperationException("Illegal operation; cannot create a new fb!");
    }

    /**
     * Internal function. Takes a provided image that has been loaded from disk and
     * does something user defined, then returns a reference RendererImage.
     * 
     * @param img Loaded image bytes
     * @param path The requested path of the image.
     * @return Reference to the image (on GPU or CPU memory)
     */
    public RendererImage INTERNAL_loadImage(BufferedImage img, String path) {
        return iRendererContext.loadImage(img, path);
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
