package velocity.renderer.erp;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import velocity.GlobalAppConfig;

/**
 * Reference counting texture context manager for LVCPU. Tracks texture references
 * and returns small, deduplicated pointers to the images.
 */
class ERPTextureContextManager {
    static final int GC_THRESHOLD = 64; // Image entries;
    HashMap<Long, ERPImageContext> imgs = new HashMap<Long, ERPImageContext>();
    HashMap<String, ERPImageContext> imgLUT = new HashMap<String, ERPImageContext>();
    long imguid = 0;

    public ERPTextureContextManager() {}

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
    
    public BufferedImage getTexture(long uid) {
        ERPImageContext ic = imgs.get(uid);
        return ic.getTexture();
    }

    public ERPRendererImage newReference(long uid) {
        ERPImageContext ic = imgs.get(uid);
        return ic.getNewHandle(this);
    }

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
                    System.out.println("[erp.tex]: Found unreferenced texture during texture GC (id "
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
