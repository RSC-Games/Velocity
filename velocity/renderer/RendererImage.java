package velocity.renderer;

import java.awt.image.BufferedImage;

/**
 * Velocity's basic image representation. Not an image in itself, it references
 * a texture stored in the renderer itself.
 */
public abstract class RendererImage {
    /**
     * Renderer's internal texture ID. Really just an index to the object in
     * the texture cache.
     */
    protected long imguid;

    /**
     * Image width. Doing a renderer lookup every time we want width is going to
     * be slightly slower than caching these values.
     */
    protected int w;

    /**
     * Image height.
     * 
     * @see w
     */
    protected int h;

    /**
     * Internally create an image reference. Not for use outside of the renderer
     * due to potentially detrimental effects to portability and rendering.
     * 
     * @param imguid Image texture identifier.
     * @param w Referenced image width
     * @param h Referenced image height
     */
    public RendererImage(long imguid, int w, int h) {
        this.imguid = imguid;
        this.w = w;
        this.h = h;
    }

    /**
     * Get the image width.
     * 
     * @return Image width.
     */
    public int getWidth() {
        return w;
    }

    /**
     * Get the image height
     * 
     * @return Image height
     */
    public int getHeight() {
        return h;
    }



    /**
     * Copy this image reference. 
     * 
     * @deprecated This does not seem to be useful and could cause reference counting issues
     * if not implemented correctly.
     * 
     * @return A new copy of this image reference.
     */
    @Deprecated(since="v0.5.2.3", forRemoval=true)
    public abstract RendererImage copy();

    /**
     * Get the underlying texture data from this reference.
     * 
     * @return The image pixel data.
     */
    public abstract BufferedImage getTexture();

    /**
     * Callback during deallocation and GC. Delete and unreference this image
     * reference. Helps the reference counter keep track of the allocated
     * and dead images.
     */
    protected abstract void unlink();
}
