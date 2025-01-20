package velocity.sprite.collision;

import velocity.*;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.*;
import velocity.util.*;

import java.awt.Color;

/**
 * A collidable, represented as a single 1 pixel wide line. Usable at any
 * angle.
 */
public class LineCollider extends Sprite implements Collidable { // Sprite
    /**
     * The internal line representation.
     */
    public final Line l;

    /**
     * Create a line collider.
     * 
     * @param pos Starting position of the line.
     * @param rot Rotation angle (ignored)
     * @param name Sprite name.
     * @param end Ending position of the line.
     */
    // TODO: Remove rotation angle parameter.
    public LineCollider(Point pos, String name, Point end) {
        super(new Transform(pos, 0, Point.zero, 0), name);
        this.l = new Line(pos, end);
    }

    /**
     * Draw this line collider on the debug renderer (with a red box).
     * 
     * @param fb The framebuffer to draw on.
     * @param d The draw transform.
     */
    @Override
    public void DEBUG_render(FrameBuffer fb, DrawInfo d) {
        Point pos = d.drawRect.getDrawLoc();
        Point start = this.l.getStart();
        Point end = this.l.getEnd();
        Point cpos = start.sub(d.drawRect.getDrawLoc());
        fb.drawLine(pos, end.sub(cpos), 1, new Color(255, 0, 0));
    }
}
