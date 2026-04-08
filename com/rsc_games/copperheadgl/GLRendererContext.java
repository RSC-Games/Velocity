package com.rsc_games.copperheadgl;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.joml.Vector3f;

import com.rsc_games.velocity.Rect;

import com.rsc_games.velocity.renderer.DrawInfo;
import com.rsc_games.velocity.renderer.RendererImage;
import com.rsc_games.velocity.util.Logger;
import com.rsc_games.velocity.util.Point;

class GLRendererContext {
    CopperheadGL renderPipeline;

    Point virtualResolution;

    /**
     * Main texture batch renderer. Text is currently rendered as a texture,
     * but will be switch to mesh-based later on.
     */
    GLTextureBatchRenderer batchRenderer;
    GLTextBatchRenderer textRenderer;
    GLPrimitiveRenderer primitiveRenderer;
    GLRectRenderer rectRenderer;
    GLFrameBuffer backBuffer;
    GLFrameBuffer uiBackBuffer;

    public GLRendererContext(int width, int height, CopperheadGL renderPipeline) {
        this.renderPipeline = renderPipeline;

        this.batchRenderer = new GLTextureBatchRenderer(this);
        this.textRenderer = new GLTextBatchRenderer(this);
        this.rectRenderer = new GLRectRenderer(this);
        this.primitiveRenderer = new GLPrimitiveRenderer();
    }

    // Standard back buffer and UI back buffer are shared.
    public GLFrameBuffer getBackBuffer() { return backBuffer; }

    /**
     * Initialize the graphics device and all subpipelines.
     * 
     * @implNote THIS FUNCTION MUST BE CALLED AFTER OPENGL DEVICE INIT.
     *   OTHERWISE, IT COULD CRASH THE KERNEL AND YOUR OPERATING SYSTEM!
     */
    public void init() {
        virtualResolution = renderPipeline.window.getVirtualResolution();

        this.backBuffer = new GLFrameBuffer(virtualResolution.x, virtualResolution.y);
        this.uiBackBuffer = new GLFrameBuffer(virtualResolution.x, virtualResolution.y);

        // Set up the expected blending system.
        glEnable(GL_BLEND);
        //glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        batchRenderer.init();
        textRenderer.init();
        rectRenderer.init();
        //primitiveRenderer.init();

        glClearColor(0f, 0f, 0f, 0f);
    }

    public void clearBuffers() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void renderFrame() {
        virtualResolution = renderPipeline.window.getVirtualResolution();

        // Reconfigure lighting engine (do in the texture renderer?)
        batchRenderer.updateLightUniforms();
        textRenderer.onRenderNextFrame();

        // Render the scene
        dispatchDrawCalls(backBuffer.getDrawQueue());
        backBuffer.flushDrawQueue();

        // Full screen shader pass

        // UI render
        dispatchDrawCalls(uiBackBuffer.getDrawQueue());
        uiBackBuffer.flushDrawQueue();        

        // Full screen UI shader pass
    }

    /**
     * Dispatch a set of drawcalls and render them.
     * 
     * @param queue Queue to consume calls from.
     */
    private void dispatchDrawCalls(GLDrawQueue drawQueue) {
        batchRenderer.start();
        textRenderer.start();
        //primitiveRenderer.start();
        rectRenderer.start();

        // TODO: remove the layer system since its made redundant by z-buffering.
        for (ArrayList<GLDrawCall> layer : drawQueue.getDrawCalls().values()) {
            for (GLDrawCall call : layer)
                executeCall(call);
        }

        batchRenderer.commit();
        textRenderer.commit();
        //primitiveRenderer.commit();
        rectRenderer.commit();
    }

    /**
     * Execute a drawcall.
     * 
     * @param call Drawcall to execute.
     */
    private void executeCall(GLDrawCall call) {
        Object[] args = call.getParameters();

        switch (call.type) {
            case DRAW_BLIT:
                batchRenderer.drawTexture((GLRendererImage)args[0], (DrawInfo)args[1]);
                break;
            case DRAW_SHADE:
                batchRenderer.drawShaded((GLRendererImage)args[0], (DrawInfo)args[1]);
                break;
            case DRAW_CIRCLE:
                Logger.warn("copper", "Received unsupported draw call: DRAW_CIRCLE");
                break;
            case DRAW_LINE:
                Logger.warn("copper", "Received unsupported draw call: DRAW_LINE");
                break;
            case DRAW_LINES:
                Logger.warn("copper", "Received unsupported draw call: DRAW_LINES");
                break;
            case DRAW_RECT:
                // Draw filled rect.
                if ((boolean)args[3])
                    break;
                else
                    rectRenderer.drawRectangle((Rect)args[0], (int)args[1], (Color)args[2]);

                break;
            case DRAW_TEXT:
                textRenderer.drawText((Point)args[0], (String)args[1], (Font)args[2], (Color)args[3]);
                break;
            case DRAW_TRI:
                Logger.warn("copper", "Received unsupported draw call: DRAW_TRI");
                break;
        }
    }

    /**
     * Get the current game resolution this renderer is targeting.
     * 
     * @return Current virtual resolution.
     */
    public Point getVirtualResolution() {
        return this.virtualResolution;
    }

    /**
     * Get the current output resolution. This is not guaranteed to be
     * the same as the virtual resolution.
     * 
     * @return Current back buffer resolution.
     */
    public Point getRenderResolution() {
        return this.renderPipeline.getWindow().getResolution();
    }

    /**
     * Convert pixel coordinates to NDC.
     * NDC conversions are done at the virtual resolution.
     * 
     * @param v Input vertex.
     * @return Transfomed coordinates.
     */
    public Vector3f toNDC(Vector3f in) {
        return new Vector3f(
            (in.x / (float)virtualResolution.x) * 2 - 1,
            -((in.y / (float)virtualResolution.y) * 2 - 1),
            in.z
        );
    }

    /**
     * Resize culling bounds for rendering.
     * 
     * @param resolution Virtual/real resolution.
     */
    public void updateResolution(Point resolution) {
        backBuffer.resize(resolution.x, resolution.y);
        uiBackBuffer.resize(resolution.x, resolution.y);
    } 

    /**
     * Internally load an image, intern it, then upload it to the GPU.
     * 
     * @param img The loaded image.
     * @param path The image path.
     * @return The loaded image.
     */
    // TODO: Switch to a path-based loading approach instead of loading the entire
    // image.
    public RendererImage loadImage(BufferedImage img, String path) {
        return this.batchRenderer.loadTexture(img, path);
    }

    /**
     * Clean out textures that are no currently being used and are wasting
     * memory.
     */
    public void runTextureGC() {
        this.batchRenderer.textureGC();
    }
}
