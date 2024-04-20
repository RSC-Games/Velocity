package velocity.renderer.erp;

import java.awt.image.BufferedImage;

/**
 * Stores an image handle. Used by the context manager to manage and interface with the render
 * pipeline and image deduplication
 */
class ERPImageContext {
    /**
     * Inner image tracked by this instance.
     */
    private BufferedImage innerb;

    /**
     * Tracker ID (for lookup and GC purposes).
     */
    private long uid;

    /**
     * How many objects and references exist to this object.
     */
    private int references = 0;

    /**
     * Create a tracked image.
     * 
     * @param b The image to track.
     * @param uid Its ID.
     */
    public ERPImageContext(BufferedImage b, long uid) {
        this.innerb = b;
        this.uid = uid;
    }

    /**
     * Create a new handle to this tracked image.
     * 
     * @param mgr The context manager that tracks this image.
     * @return A new handle to this image.
     */
    public ERPRendererImage getNewHandle(ERPTextureContextManager mgr) {
        this.references++;
        return new ERPRendererImage(uid, this.innerb.getWidth(), this.innerb.getHeight(), mgr);
    }
    
    /**
     * Get the image tracked by this instance.
     * 
     * @return The image.
     */
    public BufferedImage getTexture() {
        return this.innerb;
    }

    /**
     * Get this tracker's UID.
     * 
     * @return The tracker UID.
     */
    public long getUID() {
        return uid;
    }

    /**
     * Delete a reference to this image context.
     * 
     * @param ref The reference.
     */
    public void removeReference(ERPRendererImage ref) {
        this.references--;
    }

    /**
     * Get the current references using this image.
     * 
     * @return The reference count.
     */
    public int getReferenceCount() {
        return this.references;
    }
}
