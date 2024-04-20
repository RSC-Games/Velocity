package velocity.sprite.collision;

import velocity.*;
import velocity.renderer.FrameBuffer;
import velocity.sprite.*;
import velocity.util.*;

import java.awt.Color;

/**
 * A collidable, represented as a box of n by m dimensions.
 */
public class RectCollider extends Sprite implements Collidable {
    /**
     * Create a rectangle collider. Uses the default pos rect.
     * 
     * @param pos The origin location (center point).
     * @param name The name of the sprite.
     * @param wh The width and height of the rect.
     */
    public RectCollider(Point pos, String name, Point wh) {
        super(pos, 0f, name);
        this.pos.setWH(wh.x, wh.y);
    }

    /**
     * Draw this rect collider on screen. Drawn red since it is a static body.
     * 
     * @param fb Framebuffer to draw on.
     * @param pos The rendering location (camera pos - sprite transform)
     */
    public void DEBUG_render(FrameBuffer fb, Point pos) {
        Rect drect = new Rect(this.pos.getPos().sub(pos), this.pos.getW(), this.pos.getH());
        fb.drawRect(drect, 1, new Color(255, 0, 0), false);
    }
}
