package velocity.sprite.collision;

import java.awt.Color;

import velocity.Rect;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.*;

/**
 * Basic physics engine implementation. Does not handle force-based movement from
 * one rigidbody to another.
 */
public class PhysicsSprite extends DynamicSprite {
    private static final float GRAVITY_UNITS_PER_SECOND = 0.5f;

    /**
     * Velocity is instantaneously applied per frame.
     */
    public Vector2 velocity;

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
        this.velocity = new Vector2(Point.zero);
        this.gravityScale = gravityScale;
    }

    /**
     * Simulate physics this frame. Physics sim occurs after collision simulation.
     */
    public void simPhysics() {
        // TODO: Scale based on the time between frames for consistent simulation.
        this.velocity.y += GRAVITY_UNITS_PER_SECOND * this.gravityScale;

        // Detect environment collisions.
        if (moveDir[DynamicEntity.DIR_UP] && this.velocity.y < 0 || moveDir[DynamicEntity.DIR_DOWN] && this.velocity.y > 0)
            // Y axis motion is inverted.
            this.velocity.y = 0;

        if (moveDir[DynamicEntity.DIR_LEFT] && this.velocity.x < 0 || moveDir[DynamicEntity.DIR_RIGHT] && this.velocity.x > 0)
            this.velocity.x = 0;

        // Move the rigidbody.
        this.transform.translate(new Point(velocity));
    }

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
        fb.drawRect(drect, 1, new Color(80, 80, 255), false);
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
