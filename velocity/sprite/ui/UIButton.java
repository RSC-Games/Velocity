package velocity.sprite.ui;

import java.awt.event.MouseEvent;

import velocity.InputSystem;
import velocity.Rect;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.system.Images;
import velocity.util.Point;

/**
 * UI panel button implementation. Must be extended for usage. A base UIButton
 * is not usable as-is; event trigger information required as well.
 */
public abstract class UIButton extends UIImage {
    /**
     * The image to draw while the button is being hovered over.
     */
    RendererImage hoverImage;

    /**
     * Whether the button is currently being hovered over or not.
     */
    boolean hovered;

    /**
     * The button offset from its anchor point.
     */
    Point offset;

    /**
     * Create a UIButton.
     * 
     * @param pos Offset position from the anchor point.
     * @param rot Button rotation angle.
     * @param name The sprite name.
     * @param image The normal button image.
     * @param hoverImage The image shown while hovering.
     */
    public UIButton(Point pos, float rot, String name, String image, String hoverImage) {
        super(pos, rot, name, image);
        this.hoverImage = Images.loadImage(hoverImage);
        this.offset = pos;
    }

    /**
     * Simulate a game tick. For the button, identify whether the mouse
     * is over the button.
     */
    // TODO: Make more flexible and don't force centering for buttons.
    @Override
    public void tick() {
        this.pos.setPos(AnchorPoint.getAnchor("center").add(offset));
        Point mouseInWorldSpace = InputSystem.getMousePos();
        Rect mRect = new Rect(mouseInWorldSpace, 1, 1);

        if (this.pos.overlaps(mRect)) {
            this.hovered = true;

            if (InputSystem.released(MouseEvent.BUTTON1))
                this.clicked();
        }
        else
            this.hovered = false;
    }
    
    /**
     * Event callback when this object is clicked on.
     * Must be implemented by a subclass.
     */
    public abstract void clicked();

    /**
     * Draw this button during the UI compositing stage.
     * 
     * @param d Draw transform.
     * @param fb Rendering framebuffer.
     */
    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {
        fb.blit(this.hovered ? this.hoverImage : this.img, d);
    }
}
