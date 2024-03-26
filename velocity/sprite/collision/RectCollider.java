package velocity.sprite.collision;

import velocity.*;
import velocity.renderer.FrameBuffer;
import velocity.sprite.*;
import velocity.util.*;

import java.awt.Color;

public class RectCollider extends Sprite implements Collidable { // Sprite
    // Since no image is associated, no length & width should be necessary.
    public RectCollider(Point pos, String name, Point wh) {
        super(pos, 0f, name);
        this.pos.setWH(wh.x, wh.y);
    }

    public void DEBUG_render(FrameBuffer fb, Point pos) {
        Rect drect = new Rect(this.pos.getPos().sub(pos), this.pos.getW(), this.pos.getH());
        fb.drawRect(drect, 1, new Color(255, 0, 0), false);
    }
}
