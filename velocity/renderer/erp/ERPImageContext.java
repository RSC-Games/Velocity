package velocity.renderer.erp;

import java.awt.image.BufferedImage;

// Stores an image and managed by the context manager.
class ERPImageContext {
    private BufferedImage innerb;
    private long uid;
    private int references = 0;

    public ERPImageContext(BufferedImage b, long uid) {
        this.innerb = b;
        this.uid = uid;
    }

    public ERPRendererImage getNewHandle(ERPTextureContextManager mgr) {
        this.references++;
        return new ERPRendererImage(uid, this.innerb.getWidth(), this.innerb.getHeight(), mgr);
    }
    
    public BufferedImage getTexture() {
        return this.innerb;
    }

    public long getUID() {
        return uid;
    }

    public void removeReference(ERPRendererImage ref) {
        this.references--;
    }

    public int getReferenceCount() {
        return this.references;
    }
}
