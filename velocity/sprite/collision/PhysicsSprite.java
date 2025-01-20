package velocity.sprite.collision;

import java.awt.Color;

import velocity.Rect;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.Sprite;
import velocity.util.*;

/**
 * Basic physics engine implementation. Does not handle force-based movement from
 * one rigidbody to another.
 */
public class PhysicsSprite extends DynamicSprite {
    private static final int GRAVITY_PIXELS_PER_SECOND = 100;
    //private static final float PHYSICS_PROCESS_PER_SECOND = 60;

    /**
     * Velocity is instantaneously applied per frame.
     */
    public Point velocity;

    public float gravityScale;

    /**
     * Create a physical body.
     * 
     * @param transform Initial transform.
     * @param name Sprite name.
     * @param image The sprite image path.
     */
    public PhysicsSprite(Transform transform, String name, String image, float gravityScale) {
        super(transform, name, image);
        this.gravityScale = gravityScale;
    }

    /**
     * Simulate physics this frame. Physics sim occurs after collision simulation.
     */
    public void simPhysics() {
        this.velocity.y += GRAVITY_PIXELS_PER_SECOND * this.gravityScale;

        if (!moveDir[DynamicEntity.DIR_UP] && this.velocity.y < 0 || !moveDir[DynamicEntity.DIR_DOWN] && this.velocity.y > 0)
            // Y axis motion is inverted.
            this.velocity.y = 0;

        if (!moveDir[DynamicEntity.DIR_LEFT] && this.velocity.x < 0 || !moveDir[DynamicEntity.DIR_RIGHT] && this.velocity.x > 0)
            this.velocity.x = 0;

        // Process physics.
        this.transform.translate(this.velocity);
    }

    /**
     * Callback whenever this object hits any collider. Fires every frame
     * that this object is within the bounding box of the other collider.
     *
     * @param other Collider that was hit this frame.
     */
    public void onCollision(Sprite other) {} 

    /**
     * Callback whenever this object hits any trigger. Fires every frame
     * that this object is within the bounding box of the other trigger.
     *
     * @param other Trigger collider that was hit this frame.
     */
    public void onTrigger(Sprite other) {} 

    /**
     * Render this on the debug renderer. (Pink box showing the collider rect).
     * 
     * @param fb Input framebuffer for rendering.
     * @param info Draw transform.
     */
    @Override
    public void DEBUG_render(FrameBuffer fb, DrawInfo info) {
        super.DEBUG_render(fb, info);
        Point pos = info.drawRect.getPos();

        Rect drect = new Rect(pos, this.col.getW(), this.col.getH());
        fb.drawRect(drect, 1, new Color(255, 0, 255), false);
    }

    /**
     * Clamp a movement vector for a sprite.
     * 
     * @param mv Move vector.
     * @return Clamped move vector.
     */
    protected Point clampMove(Point mv) {
        if (moveDir[DynamicEntity.DIR_UP])
            mv.y = mv.y < 0 ? 0 : mv.y;
        if (moveDir[DynamicEntity.DIR_DOWN])
            mv.y = mv.y > 0 ? 0 : mv.y;
        if (moveDir[DynamicEntity.DIR_LEFT])
            mv.x = mv.x < 0 ? 0 : mv.x;
        if (moveDir[DynamicEntity.DIR_RIGHT])
            mv.x = mv.x > 0 ? 0 : mv.x;
        return mv;
    }
}
