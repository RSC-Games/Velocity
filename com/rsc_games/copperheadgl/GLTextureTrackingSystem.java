package com.rsc_games.copperheadgl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import com.rsc_games.velocity.config.GlobalAppConfig;
import com.rsc_games.velocity.renderer.RendererImage;
import com.rsc_games.velocity.util.Logger;

class GLTextureTrackingSystem {
    private static final int TEX_GC_THRESHOLD = 128;

    private HashMap<String, GLTextureEntry> loadedImages = new HashMap<String, GLTextureEntry>();
    private HashMap<Long, GLTextureEntry> imgLUT = new HashMap<Long, GLTextureEntry>();
    private long nextUID = 0L;

    public GLTextureTrackingSystem() {}

    /**
     * Look up an image from its path and return a handle if an image already
     * exists. 
     * 
     * @param in Input Image.
     * @param path Image path.
     * @return A reference to the image.
     */
    public GLRendererImage getInternedReference(BufferedImage in, String path) {
        GLTextureEntry entry = loadedImages.get(path);

        // If no such loaded texture is already present then save it.
        if (entry == null) {
            long uid = this.nextUID++;
            entry = new GLTextureEntry(in, new GLTexture2D(in), uid);
            loadedImages.put(path, entry);
            imgLUT.put(uid, entry);
        }

        GLRendererImage img = entry.getNewHandle();

        // Clean out unreferenced textures.
        if (imgLUT.size() >= TEX_GC_THRESHOLD)
            textureGC();

        return img;
    }

    /**
     * Determine if a provided image path has already been interned into the texture system.
     * 
     * @param path The texture path in question.
     * @return Whether the texture is present or not.
     */
    public boolean isTextureLoaded(String path) {
        return loadedImages.get(path) != null;
    }

    /**
     * Look up a texture by its path on disk and get a handle to it.
     * 
     * @param path Path to the texture on disk.
     * @return An image handle.
     */
    public GLRendererImage lookupTextureByPath(String path) {
        return loadedImages.get(path).getNewHandle();
    }

    /**
     * Perform a GC run on all of the currently loaded textures and prune
     * unreferenced ones.
     */
    public void textureGC() {
        ArrayList<Long> imgKeys = new ArrayList<Long>();
        ArrayList<String> pathKeys = new ArrayList<String>();

        // Find unreferenced textures.
        for (String key : loadedImages.keySet()) {
            GLTextureEntry entry = loadedImages.get(key);

            if (entry.getReferenceCount() == 0) {
                if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
                    Logger.log("copper", "Found unreferenced texture during texture GC (id "
                                       + entry.getUID() + ")");
                
                entry.cleanUp();
                pathKeys.add(key);
                imgKeys.add(entry.getUID());
            }
        }
        
        // Clean the deallocated entries from the file cache since they are no longer
        // in memory.
        for (String key : pathKeys) {
            loadedImages.remove(key);
        }

        // Clean the UID LUT since there is no image with that UID in memory.
        for (long uid : imgKeys) {
            imgLUT.remove(uid);
        }
    }

    public GLTexture2D getGLImage(GLRendererImage img) {
        GLTextureEntry entry = imgLUT.get(img.getUID());
        return entry.getGLTexture();
    }
}
