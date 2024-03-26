package velocity.renderer.erp;

import java.awt.image.BufferedImage;

import velocity.renderer.RendererImage;

class ERPRendererImage extends RendererImage {
    ERPTextureContextManager contextMgr;

    public ERPRendererImage(long imguid, int w, int h, ERPTextureContextManager texContextMgr) {
        super(imguid, w, h);
        this.contextMgr = texContextMgr;
    }

    public ERPRendererImage copy() {
        return contextMgr.newReference(this.imguid);
    }
    
    public BufferedImage getTexture() {
        return contextMgr.getTexture(this.imguid);
    }

    /**
     * Decrement the reference counter on this handle's image.
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
