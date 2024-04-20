package velocity.renderer.erp;

import java.awt.image.BufferedImage;

import velocity.renderer.RendererImage;

/**
 * A flexible and fast way to have many references to one image stored in memory.
 */
class ERPRendererImage extends RendererImage {
    /**
     * The owning image context manager.
     */
    ERPTextureContextManager contextMgr;

    /**
     * Create a handle for an image.
     * 
     * @param imguid The UID for the image in the texture context manager.
     * @param w The image width.
     * @param h The image height.
     * @param texContextMgr The associated texture manager.
     */
    public ERPRendererImage(long imguid, int w, int h, ERPTextureContextManager texContextMgr) {
        super(imguid, w, h);
        this.contextMgr = texContextMgr;
    }

    /**
     * Make a new reference to the image.
     * 
     * @return A new reference to the image.
     */
    @Override
    public ERPRendererImage copy() {
        return contextMgr.newReference(this.imguid);
    }
    
    /**
     * Get the texture linked to by this reference.
     * 
     * @return The actual image.
     */
    @Override
    public BufferedImage getTexture() {
        return contextMgr.getTexture(this.imguid);
    }

    /**
     * Decrement the reference counter on this handle's tracker.
     */
    protected void unlink() {
        contextMgr.removeReference(imguid, this);
    }

    /**
     * The decision to suppress the deprecation message wasn't made lightly. Since textures
     * do not have any kind of race conditions to worry about, all I want to know is when the
     * texture is unreachable. That is not affected by one of {@code finalize()}'s issues.
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        this.unlink();
    }
}
