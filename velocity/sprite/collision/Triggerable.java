package velocity.sprite.collision;

// Meant to be extended by sprites. Allows collision detection and interaction.
public interface Triggerable {
    public boolean overlaps(DynamicSprite other);
}
