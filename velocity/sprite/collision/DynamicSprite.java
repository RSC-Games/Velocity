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

public class DynamicSprite extends ImageSprite implements DynamicEntity, Collidable {
    private static final int TEST_RANGE = 5;
    public final Rect col;
    protected Point coffset = Point.zero;
    public boolean[] moveDir = new boolean[4];

    public DynamicSprite(Point pos, float rot, String name, String image) {
        super(pos, rot, name, image);
        this.col = this.pos.copy();
    }

    protected void setOffset(Point offset) {
        this.coffset = offset;
    }

    // Just allows detecting collisions and trigger hits.
    // Also enables physics.
    // NOTE: A true value in the movement array means MOVEMENT IS LOCKED!
    public void simCollide(ArrayList<Sprite> others) {
        this.col.setPos(this.pos.getPos().add(coffset));
        
        for (int i = 0; i < 4; i++) {
            moveDir[i] = false;
        }

        // Displacement or speculative, idk yet.
        for (Sprite other : others) {
            if (this == other)
                continue;

            // TODO: Calculate the current sprite's move vector from its delta
            // between last and this frame. Use this information for calculating
            // the test range in all 4 directions.
            // TODO: Make the test rects 1 pixel wide for testing purposes so they
            // don't collide with an existing wall.
            boolean hit = false;
            
            // In the future should lock a movement direction.
            if (hitN(col, other, new Point(0, -1), TEST_RANGE)) {
                hit = true;
                moveDir[DIR_UP] = true;
            }
            if (hitN(col, other, new Point(0, 1), TEST_RANGE)) {
                hit = true;
                moveDir[DIR_DOWN] = true;
            }
            if (hitN(col, other, new Point(-1, 0), TEST_RANGE)) {
                hit = true;
                moveDir[DIR_LEFT] = true;
            }
            if (hitN(col, other, new Point(1, 0), TEST_RANGE)) {
                hit = true;  
                moveDir[DIR_RIGHT] = true;
            }

            // Process a collision event if necessary.
            if (hit)
                this.onCollision(other);
        }
    }

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

    // Allows simulation of hitting trigger colliders. Since they are not
    // collidable objects, no movement locking will occur.
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
    public void onTrigger(Sprite other) {
        //System.out.println("[DynamicSprite]: Trigger collider hit; action not overridden!");
    } 

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

// Collision abstraction tree.
interface CPredicate {
    public boolean overlaps(Rect r);
}

class RectPredicate implements CPredicate {
    final Rect ir;

    public RectPredicate(Sprite o) {
        this.ir = o.pos;
    }

    public boolean overlaps(Rect r) {
        return this.ir.overlaps(r);
    }
}

class DynamicPredicate implements CPredicate {
    final Rect dr;

    public DynamicPredicate(DynamicSprite o) {
        this.dr = o.col;
    }

    public boolean overlaps(Rect r) {
        return this.dr.overlaps(r);
    }
}

class LinePredicate implements CPredicate {
    final Line il;

    public LinePredicate(LineCollider o) {
        this.il = o.l;
    }

    public boolean overlaps(Rect r) {
        return this.il.overlaps(r);
    }
}
