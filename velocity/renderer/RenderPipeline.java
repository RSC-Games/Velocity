package velocity.renderer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import velocity.Driver;
import velocity.renderer.window.Window;
import velocity.renderer.window.WindowConfig;
import velocity.shader.Shader;

/**
 * This is the backbone of the Velocity Extensible Renderer Architecture (VXRA). VXRA
 * allows arbitrary renderers and backends to plug into the rest of Velocity's engine
 * and rendering frontend code.
 */
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
     * Draws the entire scene context to one of two framebuffers on the swapchain,
     * or if no swapchain is implemented, whatever render surface is used.
     */
    public abstract void render();



    /**
     * Gets the renderer-specific draw timer created at initialization.
     * 
     * @return Renderer draw timer.
     */
    public abstract DrawTimer getTimer();

    /**
     * Generates a renderer name string. Useful for identifying the current
     * available renderer and backend.
     * 
     * @return Renderer name
     */
    public abstract String getRendererName();

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
     * Create a new framebuffer for use in the renderer. Mostly used internally but occasionally
     * used in other parts of Velocity.
     * @deprecated The utility of this function is doubted and there's no current reason to create
     * a new framebuffer. Actual removal time has not been determined.
     * 
     * @return New zeroed framebuffer.
     */
    @Deprecated(since="v0.5.2.4", forRemoval=true)
    public abstract FrameBuffer newFrameBuffer();

    /**
     * Create a new framebuffer of variable size for use in the renderer. Mostly used 
     * internally but occasionally used in other parts of Velocity.
     * @deprecated The utility of this function is doubted and there's no current reason to create
     * a new framebuffer. Actual removal time has not been determined.
     * 
     * @param x Framebuffer width
     * @param y Framebuffer height
     * @return New zeroed framebuffer.
     */
    @Deprecated(since="v0.5.2.4", forRemoval=true)
    public abstract FrameBuffer newFrameBuffer(int x, int y);

    /**
     * Internal function. Takes a provided image that has been loaded from disk and
     * does something user defined, then returns a reference RendererImage.
     * 
     * @param img Loaded image bytes
     * @param path The requested path of the image.
     * @return Reference to the image (on GPU or CPU memory)
     */
    public abstract RendererImage INTERNAL_loadImage(BufferedImage img, String path);

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
