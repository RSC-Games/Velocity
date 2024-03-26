package velocity.renderer;

import java.util.HashMap;

/**
 * Struct that represents available renderer features. Application code should take this information 
 * and either verify it meets minimum requirements or disable features that aren't supported. The creation
 * of a renderer-specific object of this class is required for correct interoperability with Velocity. 
 */
public class RendererFeatures {
    /**
     * Required for a production-grade renderer. The supplied renderer MUST support drawing primitive
     * objects and textures to the screen. A text renderer is required as well. In the event this
     * feature is missing, VXRA will automatically warn the user that the renderer does not support
     * required features. The only renderers that should disable this field are development renderers.
     */
    public final boolean FEAT_required;

    /**
     * Allows Velocity to know if this renderer uses a double-buffered rendering implementation. Not a
     * required feature but generally useful for repaint and asynchronous screen repaint along rendering.
     */
    public final boolean FEAT_doubleBuffered;

    /**
     * Allows Velocity to tell if the renderer has an implemented lighting engine. Since this is the
     * backbone of many games, most renderers should support it.
     */
    public final boolean FEAT_lighting;

    /**
     * Reports the featureset of a supplied renderer. The extended featureset requires an implemented
     * lighting engine and double buffering, as well as support for {@code FEAT_required} features.
     */
    public final boolean FEAT_extended;

    /**
     * Allows Velocity to tell if this renderer supports generic sprite fragment shaders (for cool
     * sprite-only effects) or not. Does not include the standard lighting engine.
     */
    public final boolean FEAT_spriteShaders;

    /**
     * Allows Velocity to detect if full screen post-processing fragment shaders are supported (
     * for framebuffer effects like FXAA and bloom).
     */
    public final boolean FEAT_screenShaders;

    /**
     * Reports the presence of fragment and vertex shader support for both sprites and screen
     * shading. This flag implies required feature support and extended is recommended but not
     * required. If you spend the effort to implement shader support, why isn't there lighting?
     */
    public final boolean FEAT_shaders;

    /**
     * Other renderer-specific features that were not covered in the above list.
     */
    private HashMap<String, String> extensions;



    /**
     * Construct the renderer feature list. Any additional features go in the extensions hashmap.
     * Among the required feature fields are:
     * 
     * @param required Whether this renderer supports required features. If it doesn't, VXRA will
     *   automatically warn the user or crash if fail-safe mode is disabled.
     * @param doubleBuf Whether this renderer uses a swapchain or not. Most GPU renderers based
     *   off of Velocity's rendering architecture will automatically be double buffered. Mainly just
     *   a performance optimization.
     * @param lightEngine Whether this renderer has a lighting engine. In the event that a renderer doesn't
     *   have one, most shading will have to be disabled. The sprite illumination shaders are not included
     *   in {@code FEAT_spriteShading}.
     * @param extended Whether this renderer supports the full extended feature set, which includes
     *   a lighting engine.
     * @param spriteShading Whether this renderer supports arbitrary sprite fragment shaders on a
     *   {@code FrameBuffer.drawShaded()} call or not.
     * @param screenShading Whether this renderer supports post-processing fragment shaders. LVCPU's 
     *   shader pipeline currently does not work but originally supported screen shaders.
     * @param shaders Whether this renderer supports all shader extensions or not.
     * @param extensions Other renderer-exclusive features will be supplied in here.
     */
    public RendererFeatures(boolean required, boolean doubleBuf, boolean lightEngine, boolean extended,
                            boolean spriteShading, boolean screenShading, boolean shaders,
                            HashMap<String, String> extensions) {
        
        this.FEAT_required = required;
        this.FEAT_doubleBuffered = doubleBuf;
        this.FEAT_lighting = lightEngine;
        this.FEAT_extended = extended;
        this.FEAT_spriteShaders = spriteShading;
        this.FEAT_screenShaders = screenShading;
        this.FEAT_shaders = shaders;
        this.extensions = extensions;
    }

    /**
     * Allows masking for renderer-specific features.
     * 
     * @param extName The name of the renderer extension.
     * @return Whether this renderer supports that extension.
     */
    public String getString(String extName) {
        return this.extensions.get(extName);
    }
}
