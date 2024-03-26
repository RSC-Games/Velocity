package velocity.sprite.collision;

import velocity.*;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.*;
import velocity.util.*;

import java.awt.Color;

public class LineCollider extends Sprite implements Collidable { // Sprite
    public final Line l;

    // Since no image is associated, no length & width should be necessary.
    public LineCollider(Point pos, float rot, String name, Point end) {
        super(pos, rot, name);
        this.l = new Line(pos, end);
    }

    @Override
    public void DEBUG_render(FrameBuffer fb, DrawInfo d) {
        Point pos = d.drawRect.getDrawLoc();
        Point start = this.l.getStart();
        Point end = this.l.getEnd();
        Point cpos = start.sub(d.drawRect.getDrawLoc());
        fb.drawLine(pos, end.sub(cpos), 1, new Color(255, 0, 0));
    }
}
