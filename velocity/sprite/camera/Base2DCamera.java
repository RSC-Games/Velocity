package velocity.sprite.camera;

import velocity.sprite.Camera;
import velocity.sprite.Sprite;
import velocity.util.*;

/**
 * A basic 2D Camera. Extends the current Camera system and allows tracking a
 * target.
 */
public class Base2DCamera extends Camera {
    /**
     * The target to track.
     */
    private Sprite target;

    /**
     * Damping coefficient. Normally takes 20 frames to approach the target
     * transform.
     */
    private int smoothDiv = 20;

    /**
     * Create a new camera at a given position.
     * 
     * @param pos Position for the camera.
     */
    public Base2DCamera(Point pos) {
        super(pos);
        Logger.warn("velocity.camera.Base2DCamera", "Warning: No framing target set!");
    }

    /**
     * Create a new camera at a given position following a provided tranform
     * target.
     * 
     * @param pos The camera starting location.
     * @param target The camera tracking target.
     */
    public Base2DCamera(Point pos, Sprite target) {
        super(pos);
        this.target = target;
    }

    /**
     * Change the current framing target. When there is no target then one can
     * be set.
     * 
     * @param s
     */
    public void setFramingTarget(Sprite s) {
        this.target = s;
    }

    /**
     * Change the damping coefficient. Useful for potentially changing effects
     * or follow speeds.
     * 
     * @param smoothDiv The smoothing coefficient.
     */
    public void setSmoothDiv(int smoothDiv) {
        this.smoothDiv = smoothDiv;
    }

    /**
     * Camera tick. Follow a framing target, if one is set.
     */
    public void tick() {
        super.tick();
        if (this.target == null) return;

        Point point = target.pos.getPos();
        Point distance = distanceTo(point);
        this.pos.translate(distance.div(new Point(smoothDiv, smoothDiv)));

        //float oRot = this.rot;
        //this.rot += (player.rot - this.rot) / (smoothDiv / 2);
    }

    /**
     * Calculate the distance to another point (on each axis), but leave it
     * as a 2D vector.
     * 
     * @param point Other point to calculate.
     * @return Distance to follow target.
     */
    private Point distanceTo(Point point) {
        Point loc = this.pos.getPos();
        return new Point(point.x - loc.x, point.y - loc.y);
    }
}
