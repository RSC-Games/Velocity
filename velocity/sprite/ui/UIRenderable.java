package velocity.sprite.ui;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.Sprite;
import velocity.util.Point;

/**
 * A generic renderable that can be drawn on a UI Canvas.
 */
// TODO: Allow specifying an anchor point in the base constructor so all UIObjects know how to handle
// scaling and anchoring.
public abstract class UIRenderable extends Sprite {
    /**
     * Create a UI Renderable object.
     * 
     * @param pos The renderable position.
     * @param rot The renderable rotation angle.
     * @param name The renderable's name.
     */
    public UIRenderable(Point pos, float rot, String name) {
        super(pos, rot, name);
    }

    /**
     * Render this renderable on the UI panel during the UI compositing stage.
     * 
     * @param d Draw transforms.
     * @param fb Rendering framebuffer.
     */
    public abstract void renderUI(DrawInfo d, FrameBuffer fb);
}
