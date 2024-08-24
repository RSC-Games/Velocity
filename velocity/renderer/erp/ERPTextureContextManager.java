package velocity.renderer.erp;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import velocity.config.GlobalAppConfig;
import velocity.util.Logger;

/**
 * Reference counting texture context manager for LVCPU. Tracks texture references
 * and returns small, deduplicated pointers to the images.
 */
class ERPTextureContextManager {
    /**
     * Minimum amount of textures that must be allocated before the texture GC begins running
     * automatically. Generally 64 but can be modified.
     */
    static final int GC_THRESHOLD = 64;

    /**
     * Image ID to tracker lookup. Powers most of the deduplication system.
     */
    HashMap<Long, ERPImageContext> imgs = new HashMap<Long, ERPImageContext>();

    /**
     * File path to image bytes lookup. Allows deduplication to occur even if
     * multiple of the same image are loaded into memory.
     */
    HashMap<String, ERPImageContext> imgLUT = new HashMap<String, ERPImageContext>();

    /**
     * The current available UID. Increases forever. Image UIDs are not reused within
     * a single session.
     */
    long imguid = 0;

    /**
     * Doesn't do anything.
     */
    public ERPTextureContextManager() {}

    /**
     * Hooked by {@code Images.loadImage()}. Allows the context manager to load the
     * images and deduplicate them.
     * 
     * @param img The image raster.
     * @param path The image path.
     * @return A handle to the image.
     */
    public ERPRendererImage INTERNAL_loadNewImage(BufferedImage img, String path) {
        ERPImageContext ic = imgLUT.get(path);
        
        // Avoid creating a new image for one already loaded.
        if (ic == null) {
            long uid = this.imguid++;
            ic = new ERPImageContext(img, uid);
            imgs.put(uid, ic);
            imgLUT.put(path, ic);
        }

        ERPRendererImage ref = ic.getNewHandle(this);

        if (imgs.size() > GC_THRESHOLD)
            gcRun();  // Any remaining nodes with zero references need to be trimmed.
        return ref;  
    }
    
    /**
     * Get a texture from a provided UID. Trampolined from an ERPRendererImage.
     * 
     * @param uid The UID for lookup.
     * @return The image raster.
     */
    public BufferedImage getTexture(long uid) {
        ERPImageContext ic = imgs.get(uid);
        return ic.getTexture();
    }

    /**
     * Create a new reference for a provided UID. Trampolined from an ERPRendererImage.
     * 
     * @param uid The UID for lookup.
     * @return A new reference.
     */
    public ERPRendererImage newReference(long uid) {
        ERPImageContext ic = imgs.get(uid);
        return ic.getNewHandle(this);
    }

    /**
     * Delete a created reference.
     * 
     * @param uid The UID for lookup.
     * @param ref The reference to delete.
     */
    public void removeReference(long uid, ERPRendererImage ref) {
        ERPImageContext ic = imgs.get(uid);
        ic.removeReference(ref);
    }

    /**
     * Find any and all entries with no references, and delete them. While the total 
     * loaded texture count is below 64 textures, automatic GC will not run. After
     * that threshold is hit, GC runs on every image load.
     */
    public void gcRun() {
        ArrayList<Long> imgKeys = new ArrayList<Long>();
        ArrayList<String> pathKeys = new ArrayList<String>();

        // Find unreferenced textures.
        for (String key : imgLUT.keySet()) {
            ERPImageContext ic = imgLUT.get(key);

            if (ic.getReferenceCount() == 0) {
                if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
                    Logger.log("erp.tex", "Found unreferenced texture during texture GC (id "
                                       + ic.getUID() + ")");
                
                pathKeys.add(key);
                imgKeys.add(ic.getUID());
            }
        }
        
        // Clean the deallocated entries from the file cache since they are no longer
        // in memory.
        for (String key : pathKeys) {
            imgLUT.remove(key);
        }

        // Clean the UID LUT since there is no image with that UID in memory.
        for (long uid : imgKeys) {
            imgs.remove(uid);
        }
    }
}
