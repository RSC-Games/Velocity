package velocity.shader.include;

/**
 * A simple float2 (or vec2 in GLSL) representation in Velocity. Included for
 * old LVCPU shader compatibility. Pending replacement via {@link velocity.util.Vector2}.
 */
public class Float2 {
    /**
     * X location.
     */
    public float x;

    /**
     * Y location.
     */
    public float y;

    /**
     * Create a Float2
     * 
     * @param x X location
     * @param y Y location.
     */
    public Float2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return this object's string representation.
     * 
     * @return The string representation.
     */
    public String toString() {
        return "float2(" + this.x + ", " + this.y + ")";
    }
}
