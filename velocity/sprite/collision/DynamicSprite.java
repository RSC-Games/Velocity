package velocity.sprite.collision;

import java.util.ArrayList;
import java.awt.Color;

import velocity.Line;
import velocity.Rect;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.ImageSprite;
import velocity.sprite.Sprite;
import velocity.util.*;

/**
 * Velocity's standard implementation of the DynamicEntity system. Handles collision
 * events and enforces collision constraints.
 */
public class DynamicSprite extends ImageSprite implements DynamicEntity, Collidable {
    /**
     * Maximum range (in pixels) of collision testing. This current testing system
     * is inherently buggy due to how test ranges are handled.
     */
    private static final int TEST_RANGE = 5;

    /**
     * Collider rectangle representation.
     */
    public final Rect col;

    /**
     * Offset for the collider rect.
     */
    protected Point coffset = Point.zero;

    /**
     * Move directions as specified by DynamicEntity.
     * @see velocity.sprite.collision.DynamicEntity DynamicEntity
     */
    public boolean[] moveDir = new boolean[4];

    /**
     * Body location last frame.
     */
    private Point lastFramePos;

    /**
     * Create a Dynamic Sprite.
     * 
     * @param pos Initial position.
     * @param rot Rotation angle.
     * @param name Sprite name.
     * @param image The sprite image path.
     */
    public DynamicSprite(Transform transform, String name, String image) {
        super(transform, name, image);
        this.col = this.transform.location.copy();
        this.lastFramePos = transform.getPosition();
    }

    /**
     * Set the collider rect offset.
     * 
     * @param offset Rect offset.
     */
    protected void setOffset(Point offset) {
        this.coffset = offset;
    }

    /**
     * Simulate collision with nearby collidables and note directions in which some
     * are hit.
     * 
     * @param others Collidables for simulation.
     */
    @Override
    public void simCollide(ArrayList<Sprite> others) {
        this.col.setPos(this.transform.getPosition().add(coffset));
        
        // NOTE: A true value in the movement array means MOVEMENT IS LOCKED!
        for (int i = 0; i < 4; i++) {
            moveDir[i] = false;
        }

        // Determine the absolute direction this body is moving.
        // Required for properly displacing the body.
        Point movementDelta = this.transform.getPosition().sub(lastFramePos);
        
        if (movementDelta.x != 0 || movementDelta.y != 0)
            System.out.println("movement delta " + movementDelta);

        // Speculative collision system -- displacement not fully implemented.
        for (Sprite other : others) {
            if (this == other)
                continue;

            // TODO: Collider distance culling -- avoid more expensive collision testing
            // if the colliders arent even nearby.

            // TODO: Make the test rects 1 pixel wide for testing purposes so they
            // don't collide with an existing wall.
            boolean hit = false;

            // TODO: Switch the current collision engine to use a sweep test-based collision system,
            // then cap movement to the point of intersection based on the movement vector.

            // SECTION 5: Fast-Moving Objects

            // As mentioned above, small and/or fast-moving objects can produce problems when 
            // using a static collision test. There are several approaches that can be taken to 
            // handle such objects -- the simplest is to constrain your game design so that such 
            // objects aren't needed.

            // If you absolutely must have them, there are two common methods to deal with small 
            // and/or fast-moving objects: swept-collision tests, and multisampling.

            // --= sweep tests =--

            // Instead of testing for intersection between two static shapes, we can instead create 
            // new shapes by sweeping the original shapes along their trajectory, and testing for 
            // overlap between these swept shapes.

            // The basic idea is described in [Gomez], for circle-circle and AABB-AABB sweep tests.

            // --= multisampling =--

            // A much simpler alternative to swept tests is to multisample; instead of performing a 
            // single static test at the object's new position, perform several tests at several 
            // positions located between the object's previous and new position. This technique was 
            // used to collide the ragdoll in N.

            // If you make sure that the samples are always spaced at distances less than the 
            // object's radius, this will produce excellent results. In our implementation, we 
            // limit the maximum number of samples, so very high speeds will sometimes result in 
            // problems; this is something that can be tweaked based on your specific application.
            Point colPos = col.getPos();
            Point halfSz = col.getWH().div(2);
            
            // In the future should lock a movement direction.
            System.out.println("object " + this.name);
            System.out.println("main collider " + col);
            Rect hitCol = new Rect(colPos.sub(new Point(0, halfSz.y)), new Point(col.getW() - 2, 2));
            if (hitN(hitCol, other, new Point(0, -1), movementDelta.y < 0 ? Math.abs(movementDelta.y) : 1)) {
                System.out.println("Hit thing above");
                hit = true;
                moveDir[DIR_UP] = true;
            }

            System.out.println("up col " + hitCol + " range " + (movementDelta.y < 0 ? movementDelta.y : 1));

            hitCol = new Rect(colPos.add(new Point(0, halfSz.y)), new Point(col.getW() - 2, 2));
            if (hitN(hitCol, other, new Point(0, 1), movementDelta.y > 0 ? Math.abs(movementDelta.y) : 1)) {
                //System.out.println("Hit thing below");
                hit = true;
                moveDir[DIR_DOWN] = true;
            }

            System.out.println("down col " + hitCol + " range " + (movementDelta.y > 0 ? movementDelta.y : 1));

            hitCol = new Rect(colPos.sub(new Point(halfSz.x, 0)), new Point(2, col.getH() - 2));
            if (hitN(hitCol, other, new Point(-1, 0), movementDelta.x < 0 ? Math.abs(movementDelta.x) : 1)) {
                System.out.println("Hit thing left");
                hit = true;
                moveDir[DIR_LEFT] = true;
            }

            System.out.println("left col " + hitCol + " range " + (movementDelta.x > 0 ? movementDelta.x : 1));

            hitCol = new Rect(colPos.add(new Point(halfSz.x, 0)), new Point(2, col.getH() - 2));
            if (hitN(hitCol, other, new Point(1, 0), movementDelta.x > 0 ? Math.abs(movementDelta.x) : 1)) {
                System.out.println("Hit thing right");
                hit = true;  
                moveDir[DIR_RIGHT] = true;
            }

            System.out.println("right  col " + hitCol + " range " + (movementDelta.x < 0 ? movementDelta.x : 1));

            // Process a collision event if necessary.
            if (hit)
                this.onCollision(other);
        }

        this.lastFramePos = this.transform.getPosition();
    }

    /**
     * Identify if this object can move in a provided direction.
     * @see velocity.sprite.collision.DynamicSprite DynamicSprite.
     * 
     * @param direction A direction code from DynamicSprite.
     * @return Whether this can move in that direction.
     */
    public boolean canMoveDirection(int direction) {
        return !moveDir[direction];
    }

    /**
     * Identify if this object is touching ground.
     * 
     * @return If this sprite cannot move downwards.
     */
    public boolean touchingGround() {
        return moveDir[DIR_DOWN];
    }

    /**
     * Attempt to hit a provided collider rect in maximum steps.
     * 
     * @param r The collider rect.
     * @param other The other sprite to simulate.
     * @param step The size of each simulated step.
     * @param steps Maximum step count.
     * @return Whether the collidable was hit or not.
     */
    private boolean hitN(Rect r, Sprite other, Point step, int steps) {
        CPredicate p;
        Rect tr = r.copy();

        if (other instanceof LineCollider)
            p = new LinePredicate((LineCollider)other);
        else if (other instanceof DynamicSprite)
            p = new DynamicPredicate((DynamicSprite)other);
        else
            p = new RectPredicate(other);

        for (int i = 0; i < steps; i++) {
            tr.translate(step);

            // Hit the nearest collision target.
            if (p.overlaps(tr)) {
                return true;
            }
        }

        // Nothing in range.
        return false;
    }

    /**
     * Allows simulation of hitting trigger colliders. Since they are not
     * collidable objects, no movement locking will occur.
     * 
     * @param others Other triggerables to simulate.
     */
    public void simTrigger(ArrayList<Sprite> others) {
        for (Sprite other : others) {
            if (this == other)
                continue;

            Triggerable ot = (Triggerable)other;
            
            // In the future should lock a movement direction.
            if (ot.overlaps(this)) {
                this.onTrigger(other);
            }
        }
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

/**
 * Collision abstraction tree for testing between different collider types.
 */
interface CPredicate {
    /**
     * Test if a given collider overlaps with a given rect.
     * 
     * @param r Other rectangle.
     * @return Whether they overlap.
     */
    public boolean overlaps(Rect r);
}

/**
 * Rect to rect version of the collision tree.
 */
class RectPredicate implements CPredicate {
    final Rect ir;

    public RectPredicate(Sprite o) {
        this.ir = o.transform.location;
    }

    public boolean overlaps(Rect r) {
        return this.ir.overlaps(r);
    }
}

/**
 * Dynamic body predicate (changes collision geometry)
 */
class DynamicPredicate implements CPredicate {
    final Rect dr;

    public DynamicPredicate(DynamicSprite o) {
        this.dr = o.col;
    }

    public boolean overlaps(Rect r) {
        return this.dr.overlaps(r);
    }
}

/**
 * Line to rect version of the collision tree.
 */
class LinePredicate implements CPredicate {
    final Line il;

    public LinePredicate(LineCollider o) {
        this.il = o.l;
    }

    public boolean overlaps(Rect r) {
        return this.il.overlaps(r);
    }
}
