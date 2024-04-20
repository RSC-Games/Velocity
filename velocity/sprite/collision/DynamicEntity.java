package velocity.sprite.collision;

import java.util.ArrayList;

import velocity.sprite.Sprite;

/**
 * The default rigid body system, without a physics engine. Displacement is not
 * handled.
 */
public interface DynamicEntity {
    public static final int DIR_UP = 0;
    public static final int DIR_DOWN = 1;
    public static final int DIR_LEFT = 2;
    public static final int DIR_RIGHT = 3;

    /**
     * Allows the implementer to detect collisions and trigger hits. Enables physics.
     * 
     * @implNote Implementers require moveDir field but cannot be put here for functionality 
     * reasons. Add {@code public boolean[] moveDir = new boolean[4]; } for the intended effect.
     * The actual collision is handled by {@code DynamicSprite}.
     * @see velocity.sprite.collision.DynamicSprite DynamicSprite, an implementer.
     * 
     * @param others Other collidable objects.
     */
    public void simCollide(ArrayList<Sprite> others);
}
