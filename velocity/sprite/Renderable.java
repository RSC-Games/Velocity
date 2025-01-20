package velocity.sprite;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.*;

/**
 * Basic renderable sprite. All sprites are automatically positioned
 * on screen relative to the current camera transform.
 */
public abstract class Renderable extends Sprite {
    /**
     * Create a renderable.
     * 
     * @param pos The renderable position.
     * @param rot The rotation angle.
     * @param name The name of the renderable.
     */
    public Renderable(Transform transform, String name) {
        super(transform, name);
    }

    /**
     * Draw the renderable on screen.
     * 
     * @param d The fully resolved sprite location in camera space.
     * @param fb The rendering framebuffer.
     */
    public abstract void render(DrawInfo d, FrameBuffer fb);
}
