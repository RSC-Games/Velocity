package velocity.sprite.ui;

import java.awt.event.MouseEvent;

import velocity.Images;
import velocity.InputSystem;
import velocity.Rect;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.util.Point;

public abstract class UIButton extends UIImage {
    RendererImage hoverImage;
    boolean hovered;
    Point offset;

    public UIButton(Point pos, float rot, String name, String image, String hoverImage) {
        super(pos, rot, name, image);
        this.hoverImage = Images.loadImage(hoverImage);
        this.offset = pos;
    }

    public void tick() {
        this.pos.setPos(AnchorPoint.getAnchor("center").add(offset));
        Point mouseInWorldSpace = InputSystem.getMousePos();
        Rect mRect = new Rect(mouseInWorldSpace, 1, 1);

        if (this.pos.overlaps(mRect)) {
            this.hovered = true;

            if (InputSystem.clicked(MouseEvent.BUTTON1))
                this.clicked();
        }
        else
            this.hovered = false;
    }
    
    /**
     * Fired when this object is clicked on.
     */
    public abstract void clicked();

    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {
        fb.blit(this.hovered ? this.hoverImage : this.img, d);
    }
}
