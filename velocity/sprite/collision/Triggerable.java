package velocity.sprite.collision;

/**
 * Informs Velocity that the implementer is a trigger collider (a collider
 * that can register collision but doesn't obstruct movement.
 */
public interface Triggerable {
    /**
     * Simulate an overlap test.
     * 
     * @param other The dynamic sprite to test against.
     * @return Whether they overlap or not.
     */
    public boolean overlaps(DynamicSprite other);
}
