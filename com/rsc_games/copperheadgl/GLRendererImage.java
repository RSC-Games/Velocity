package com.rsc_games.copperheadgl;

import java.awt.image.BufferedImage;

import com.rsc_games.velocity.renderer.RendererImage;

public class GLRendererImage extends RendererImage {
    private GLTextureEntry referent;

    /**
     * Create a reference to an image with this renderer.
     * 
     * @param imguid The internal image uid.
     * @param w Image width.
     * @param h Image height.
     */
    public GLRendererImage(long imguid, int w, int h, GLTextureEntry cacheEntry) {
        super(imguid, w, h);
        this.referent = cacheEntry;
    }

    /**
     * Copy this image and its handle.
     */
    @Override
    public RendererImage copy() {
        throw new UnsupportedOperationException("unsupported op 'copy'");
    }

    /**
     * Get the image internal texture.
     */
    @Override
    public BufferedImage getTexture() {
        throw new UnsupportedOperationException("Unimplemented method 'getTexture'");
    }

    /**
     * Deregister this image reference.
     */
    @Override
    protected void unlink() {
        //System.out.println("[lvogl]: Deleting image reference " + this);
        referent.deleteReference(this);
    }

    /**
     * Prevent any possible resource leakage.
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        this.unlink();
    }

    public long getUID() {
        return this.imguid;
    }
    
}
