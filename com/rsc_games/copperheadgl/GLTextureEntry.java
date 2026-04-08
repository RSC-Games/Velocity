package com.rsc_games.copperheadgl;

import java.awt.image.BufferedImage;

import com.rsc_games.velocity.util.Logger;

class GLTextureEntry {
    private BufferedImage cachedImage;
    private GLTexture2D glTexEntry;
    private int refCount = 0;
    private long uid;

    /**
     * Create a new texture cache entry.
     * 
     * @param inner Represented BufferedImage
     * @param glTex Handle to the GPU instanced BufferedImage.
     * @param uid Texture ID.
     */
    public GLTextureEntry(BufferedImage inner, GLTexture2D glTex, long uid) {
        this.cachedImage = inner;
        this.glTexEntry = glTex;
        this.uid = uid;
    }

    /**
     * Create a new handle to this renderer image.
     * @return
     */
    public GLRendererImage getNewHandle() {
        refCount++;
        GLRendererImage img = new GLRendererImage(uid, cachedImage.getWidth(), 
                                                    cachedImage.getHeight(), this);
        return img;
    }

    /**
     * Get the internal GPU texture reference.
     * 
     * @return GL Texture.
     */
    public GLTexture2D getGLTexture() {
        return this.glTexEntry;
    }

    /**
     * Delete a reference to this cache entry.
     * 
     * @param img Image to delete the reference.
     */
    public void deleteReference(GLRendererImage img) {
        this.refCount--;
    }

    /**
     * Get this current entry's reference count.
     * 
     * @return Current reference count.
     */
    public int getReferenceCount() {
        return this.refCount;
    }

    /**
     * Currently exposing the internal texture is not known. To actually allow
     * CPU + GPU access, I will have to deallocate the old texture entry on
     * the GPU and add a new one.
     * 
     * @throws UnsupportedOperationException Not currently supported.
     * @return This internal image.
     */
    public BufferedImage getTexture() {
        return cachedImage;
    }

    /**
     * Get this images's UID.
     * 
     * @return The image UID.
     */
    public long getUID() {
        return uid;
    }

    /**
     * When this entry is deleted allocated GPU and CPU memory needs to be
     * released.
     */
    public void cleanUp() {
        Logger.log("copper", "Freeing texture entry for resource " + uid);
        glTexEntry.free();
    }
}
