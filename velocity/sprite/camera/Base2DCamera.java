package velocity.sprite.camera;

import velocity.sprite.Camera;
import velocity.sprite.Sprite;
import velocity.util.*;

public class Base2DCamera extends Camera {
    private Sprite target;
    private int smoothDiv = 20;

    public Base2DCamera(Point pos) {
        super(pos);
    }

    public Base2DCamera(Point pos, Sprite target) {
        super(pos);
        this.target = target;
    }

    public void setFramingTarget(Sprite s) {
        this.target = s;
    }

    public void setSmoothDiv(int smoothDiv) {
        this.smoothDiv = smoothDiv;
    }

    public void tick() {
        super.tick();

        if (this.target == null) {
            System.out.println("[engine.sprite.Base2DCamera]: Warning: No framing target set!");
            return;
        }

        Point point = target.pos.getPos();
        Point distance = distanceTo(point);
        this.pos.translate(distance.div(new Point(smoothDiv, smoothDiv)));

        //float oRot = this.rot;
        //this.rot += (player.rot - this.rot) / (smoothDiv / 2);
    }

    private Point distanceTo(Point point) {
        Point loc = this.pos.getPos();
        return new Point(point.x - loc.x, point.y - loc.y);
    }
}
