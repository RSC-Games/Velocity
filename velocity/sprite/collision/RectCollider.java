package velocity.sprite.collision;

import velocity.*;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.*;
import velocity.util.*;

import java.awt.Color;
import java.awt.Font;

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
    public RectCollider(Transform transform, String name) {
        super(transform, name);
    }

    /**
     * Draw this rect collider on screen. Drawn red since it is a static body.
     * 
     * @param fb Framebuffer to draw on.
     * @param pos The rendering location (camera pos - sprite transform)
     */
    @Override
    public void DEBUG_render(FrameBuffer fb, DrawInfo info) {
        Font font = new Font(Font.SERIF, 0, 15);

        Point widthHeight = this.transform.location.getWH();
        Point pos = info.drawRect.getPos();

        Rect drect = new Rect(pos, widthHeight.x, widthHeight.y);
        fb.drawRect(drect, 1, new Color(255, 0, 0), false);
        fb.drawText(drect.getPos(), this.name, font, Color.red);
    }
}
