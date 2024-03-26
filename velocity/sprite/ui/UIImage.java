package velocity.sprite.ui;

import velocity.Images;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.util.*;

public abstract class UIImage extends UIRenderable {
    public RendererImage img;

    public UIImage(Point pos, float rot, String name, String imagename) {
        super(pos, rot, name);
        this.img = Images.loadImage(imagename);
        this.pos.setWH(img.getWidth(), img.getHeight());
    }

    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {
        fb.blit(this.img, d);
    }

    public void finalize() {
        super.finalize();
        System.out.println("[Scene.GC{UIImage}]: Unlinking image " + img);
        this.img = null;  // Completely unneccessary since the image unlinks itself anyway.
    }
}
