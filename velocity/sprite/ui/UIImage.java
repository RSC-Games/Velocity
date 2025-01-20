package velocity.sprite.ui;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.system.Images;
import velocity.util.*;

/**
 * Basic image rendered on a UI panel.
 */
public abstract class UIImage extends UIRenderable {
    /**
     * The image to render.
     */
    public RendererImage img;

    /**
     * Create a UIImage.
     * 
     * @param pos The offset transform for the image.
     * @param rot The image rotation angle.
     * @param name The name of the sprite.
     * @param imagename The image filepath.
     */
    public UIImage(Transform transform, String name, String imagename) {
        super(transform, name);
        this.img = Images.loadImage(imagename);
        this.transform.updateRect(new Point(img.getWidth(), img.getHeight()));
    }

    /**
     * Create a UIImage, and directly provide an image.
     * 
     * @param pos The offset transform for the image.
     * @param rot The image rotation angle.
     * @param name The name of the sprite.
     * @param imageHnd The image reference.
     */
    public UIImage(Transform transform, String name, RendererImage imageHnd) {
        super(transform, name);
        this.img = imageHnd;
        this.transform.updateRect(new Point(img.getWidth(), img.getHeight()));
    }

    /**
     * Draw the image on screen.
     * 
     * @param d Draw transform.
     * @param fb Rendering framebuffer.
     */
    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {
        fb.blit(this.img, d);
    }
}
