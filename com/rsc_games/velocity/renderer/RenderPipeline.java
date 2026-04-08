package com.rsc_games.velocity.renderer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.rsc_games.velocity.Driver;
import com.rsc_games.velocity.renderer.window.Window;
import com.rsc_games.velocity.renderer.window.WindowConfig;
import com.rsc_games.velocity.shader.Shader;

/**
 * This is the backbone of the Velocity Extensible Renderer Architecture (VXRA). VXRA
 * allows arbitrary renderers and backends to plug into the rest of Velocity's engine
 * and rendering frontend code.
 */

// TODO: Reabstract the lighting engine to reduce engine complexity.
// TODO: Add support for multiple render targets.
public abstract class RenderPipeline {
    /**
     * Provides illumination and lighting functions, a lot of which are not directly used.
     * Consult the {@code LightingEngine} abstract class for more details.
     */
    public final LightingEngine le;

    /** 
     * Provides crucial featureset information to Velocity. Must be instantiated and set
     * in a subclass.
     */
    protected RendererFeatures featureSet;

    /**
     * Internally used for shader pipeline management. The API here has not fully matured,
     * but {@code preUIShaders} runs before UI compositing. {@code fullScreenShaders} shades
     * everything, including the UI panel.
     */
    protected ArrayList<Shader> preUIShaders = new ArrayList<Shader>();

    /**
     * See {@code preUIShaders} for more details.
     */
    protected ArrayList<Shader> fullScreenShaders = new ArrayList<Shader>();

    /**
     * Renderer call hook for the repaint handler. Mostly managed internally.
     */
    protected Driver m;

    /**
     * Internal creation of the renderer. Must always be called by an extension
     * renderer's constructor.
     * 
     * @param m Takes a driver class (always {@code Main}) for frame events.
     * @param le Basic or advanced lighting engine bundled with the renderer.
     */
    public RenderPipeline(Driver m, LightingEngine le) {
        this.le = le;
        this.m = m;
    }

    /**
     * Creates a renderer context. Links required listeners and generates a frame
     * from the supplied config. A driver is required for linking to the internal
     * {@code DrawTimer}. Always must be implemented by a subclass renderer.
     * 
     * @param windowCfg Window configuration parameters.
     * @param m Driver class (which is always {@code Main}).
     */
    public RenderPipeline(WindowConfig windowCfg, Driver m) { le = null; }

    /**
     * Initializes this render pipeline. Instead of doing most init in the constructor,
     * where many libs may not be ready for a fully instantiated renderer, this is a better
     * place to do it.
     */
    public abstract void init();

    /**
     * Deinitializes the render pipeline. Should be used to clean up renderer resources
     * and deallocate memory when velocity is shutting down.
     */
    public abstract void deinit();

    /**
     * Draws the entire scene context to one of two framebuffers on the swapchain,
     * or if no swapchain is implemented, whatever render surface is used.
     */
    public abstract void render();

    /**
     * Runs in the render thread while the render thread is idle. Guaranteed
     * to be called at least once per loop.
     */
    public abstract void renderIdle();



    /**
     * Generates a renderer name string. Useful for identifying the current
     * available renderer and backend.
     * 
     * @return Renderer name
     */
    public abstract String getRendererName();

    /**
     * Get the VXRA version the extension renderer was designed to target.
     * This prevents obscure crashes from renderer function mismatches.
     * 
     * @return Extension renderer VXRA version string.
     */
    public abstract String getVXRATargetVersion();

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
    public abstract Window getWindow();



    /**
     * Internal function. Queries the renderer cache for the texture associated with
     * the provided file path. Ideally used to reduce disk I/O time.
     * 
     * @param path Texture path.
     * @return Whether the provided texture has already been loaded.
     */
    public abstract boolean isTextureCached(String path);

    /**
     * Send a loaded texture to the render pipeline for handling. The plugin renderer
     * is expected to handle this intelligently (and ideally make it part of a texture
     * cache).
     * 
     * @param image The loaded image from disk.
     * @param path The image path (for caching purposes).
     * @return Whether the image was successfully loaded or not.
     */
    public abstract boolean registerTexture(BufferedImage image, String path);

    /**
     * Get the texture handle from the provided image path. This function should only
     * fetch from the render cache.
     * 
     * @param path The image path.
     * @return The image handle.
     */
    public abstract RendererImage getTextureHandleFromPath(String path);

    /**
     * By default in LumaViper CPU the texture GC only runs once you have 256 textures 
     * loaded in texture memory. This behavior may vary across rendering backends, but
     * in any case, if there are a bunch of unreferenced textures eating up memory, this
     * will force the texture cache to erase them. Then it's up to the JVM GC to clear it.
     */
    public abstract void forceGCRun();



    /** 
     * Shader installation API. Not currently fully supported. 
    */
    // TODO: Shading API is very badly implemented and should be reabstracted.
    public void applyShaderFull(Shader shader) {
        fullScreenShaders.add(shader);
    }

    public void applyShaderPreUI(Shader shader) {
        preUIShaders.add(shader);
    }

    public void removeShaderFull(String id) {
        for (int i = 0; i < fullScreenShaders.size(); i++) {
            if (fullScreenShaders.get(i).getClass().getSimpleName().equals(id)) {
                fullScreenShaders.remove(i);
                return;
            }
        }
    }

    public void removeShaderPreUI(String id) {
        for (int i = 0; i < preUIShaders.size(); i++) {
            if (preUIShaders.get(i).getClass().getSimpleName().equals(id)) {
                preUIShaders.remove(i);
                return;
            }
        }
    }
}
