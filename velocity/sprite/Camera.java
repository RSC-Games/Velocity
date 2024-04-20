package velocity.sprite;

import velocity.util.*;

/**
 * The base camera implementation of Velocity. Essential for rendering.
 */
public class Camera extends Sprite {
    /**
     * Current window resolution. One of the most important lines of code in
     * the entirety of Velocity (and importance being determined by usage).
     */
    // TODO: Inflexible and not containerized.
    public static Point res = new Point(1, 1);

    /**
     * Create a standard camera.
     * 
     * @param pos Camera starting position.
     */
    public Camera(Point pos) {
        super(pos, 0, "Main Camera");
        this.pos.setWH(Camera.res.x, Camera.res.y);
    }

    /**
     * Update the local camera resolution when the screen resolution changes.
     */
    public void tick() {
        // BUGFIX (Velocity v0.1.0.0): Workaround for bad camera scaling.
        this.pos.setWH(Camera.res.x, Camera.res.y);
    }
}
