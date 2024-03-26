package velocity.sprite;

import velocity.GlobalAppConfig;
import velocity.Images;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.util.Point;

public class ImageSprite extends Renderable {
    protected RendererImage img;

    public ImageSprite(Point pos, float rot, String name, String image) {
        super(pos, rot, name);

        // Only load an image if provided
        if (image != null) {
            this.img = Images.loadImage(image);
            this.pos.setWH(img.getWidth(), img.getHeight());
        }
    }

    public ImageSprite(Point pos, float rot, String name, RendererImage image) {
        super(pos, rot, name);

        // Only use image properties if one is provided.
        this.img = image;
        if (image != null)
            this.pos.setWH(img.getWidth(), img.getHeight());
    }

    @Override
    public void render(DrawInfo d, FrameBuffer fb) {
        fb.blit(this.img, d);
    }

    @Override
    public void DEBUG_render(FrameBuffer fb, DrawInfo info) {
        fb.blit(this.img, info);
    }

    public RendererImage getImg() {
        return this.img;
    }

    public void finalize() {
        super.finalize();
        if (GlobalAppConfig.bcfg.LOG_GC)
            System.out.println("[Scene.GC{ImageSprite}]: Unlinking image " + img);
            
        this.img = null;  // Probably 100% unneccessary since the image unlinks itself anyway.
    }
}
