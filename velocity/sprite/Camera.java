package velocity.sprite;

import velocity.util.*;

public class Camera extends Sprite {
    // Just a special sprite
    public static Point res = new Point(1, 1);

    public Camera(Point pos) {
        super(pos, 0, "Main Camera");
        this.pos.setWH(Camera.res.x, Camera.res.y);
    }

    // BUGFIX (Velocity v0.1.0.0): Workaround for bad camera scaling.
    public void tick() {
        this.pos.setWH(Camera.res.x, Camera.res.y);
    }
}
