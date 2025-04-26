package velocity.sprite;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.system.Images;
import velocity.util.Point;
import velocity.util.Transform;

/**
 * A basic sprite with an image that can be drawn on-screen.
 */
public class ImageSprite extends Renderable {
    /**
     * The image to draw.
     */
    protected RendererImage img;

    /**
     * Create an image sprite.
     * 
     * @param pos The center position of the sprite.
     * @param rot The image angle.
     * @param name The name of the sprite.
     * @param image The image path.
     */
    public ImageSprite(Transform transform, String name, String image) {
        super(transform, name);

        // Only load an image if provided
        if (image != null) {
            this.img = Images.loadImage(image);
            this.transform.updateRect(new Point(img.getWidth(), img.getHeight()));
        }
    }

    /**
     * Alternate constructor. Allows passing in a loaded image instead of
     * a file path.
     * 
     * @param pos The center position.
     * @param rot The rotation angle.
     * @param name The name of the sprite.
     * @param image The image reference.
     */
    public ImageSprite(Transform transform, String name, RendererImage image) {
        super(transform, name);

        // Only use image properties if one is provided.
        this.img = image;
        if (image != null)
            this.transform.updateRect(new Point(img.getWidth(), img.getHeight()));
    }

    /**
     * Draw the image on screen.
     * 
     * @param d Draw transform
     * @param fb Rendering framebuffer.
     */
    @Override
    public void render(DrawInfo d, FrameBuffer fb) {
        fb.blit(this.img, d);
    }

    /**
     * Draw the image on the debug renderer.
     * 
     * @param fb Rendering framebuffer.
     * @param info Draw transform
     */
    @Override
    public void DEBUG_render(FrameBuffer fb, DrawInfo info) {
        //fb.blit(this.img, info);
    }

    /**
     * Get the renderer image this sprite uses.
     * 
     * @return The image.
     */
    public RendererImage getImg() {
        return this.img;
    }
}
