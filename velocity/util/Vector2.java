package velocity.util;

/**
 * General point representation used across Velocity. Allows for 
 * more accurate floating point math.
 */
public class Vector2 {
    /**
     * Zero point.
     */
    public static final Vector2 zero = new Vector2(0, 0);

    /**
     * All values initialized to 1.
     */
    public static final Vector2 one = new Vector2(1, 1);

    /**
     * 2D point that points upward.
     */
    public static final Vector2 up = new Vector2(0, -1);

    /**
     * 2D Point pointing downward.
     */
    public static final Vector2 down = new Vector2(0, 1);

    /**
     * 2D Point pointing right.
     */
    public static final Vector2 right = new Vector2(1, 0);

    /**
     * 2D Point pointing left.
     */
    public static final Vector2 left = new Vector2(-1, 0);

    /**
     * X location in pixels.
     */
    public float x;

    /**
     * Y location in pixels.
     */
    public float y;

    /**
     * Create a point at the given location. Note that increased y values
     * go further down as they increase.
     * 
     * @param x X location
     * @param y Y location
     */
    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a point at the given location. Note that increased y values
     * go further down as they increase.
     * 
     * @param x X location
     * @param y Y location
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a point at the given location. Note that increased y values
     * go further down as they increase.
     * 
     * @param p Input point.
     */
    public Vector2(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Add this and another point together.
     * 
     * @param other The other point to add.
     * @return The new combined points.
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    /**
     * Add this point to a scalar and make a new one.
     * 
     * @param other The scalar value to add
     * @return The new point moved.
     */
    public Vector2 add(float other) {
        return new Vector2(this.x + other, this.y + other);
    }

    /**
     * Subtract this point from another another point.
     * 
     * @param other The other point to subtract.
     * @return The new transformed points.
     */
    public Vector2 sub(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    /**
     * Subtract this point to a scalar and make a new one.
     * 
     * @param other The scalar value to subtract
     * @return The new point transformed.
     */
    public Vector2 sub(float other) {
        return new Vector2(this.x - other, this.y - other);
    }

    /**
     * Multiply two points together.
     * 
     * @param other The other point to multiply.
     * @return The new transformed points.
     */
    public Vector2 mult(Vector2 other) {
        return new Vector2(this.x * other.x, this.y * other.y);
    }

    /**
     * Multiply this point and a scalar together.
     * 
     * @param other The scalar to multiply.
     * @return The new transformed points.
     */
    public Vector2 mult(float other) {
        return new Vector2(this.x * other, this.y * other);
    }

    /**
     * Divide this point by another.
     * 
     * @param other Other point to divide by.
     * @return The new transformed points.
     */
    public Vector2 div(Vector2 other) {
        return new Vector2(this.x / other.x, this.y / other.y);
    }

    /**
     * Divide this point by a scalar value.
     * 
     * @param other The scalar to divide by.
     * @return The new transformed points.
     */
    public Vector2 div(float other) {
        return new Vector2(this.x / other, this.y / other);
    }

    /**
     * Get the modulus of this point.
     * 
     * @param other The other point.
     * @return The new transformed point.
     */
    public Vector2 mod(Vector2 other) {
        return new Vector2(this.x % other.x, this.y % other.y);
    }

    /**
     * Get the modulus of this point by a scalar.
     * 
     * @param other The scalar value.
     * @return The new transformed point.
     */
    public Vector2 mod(float other) {
        return new Vector2(this.x % other, this.y % other);
    }

    /**
     * Get the distance, to the nearest int, of this point from
     * another point.
     * 
     * @param other The destination.
     * @return Distance to the other point.
     */
    public float distanceTo(Vector2 other) {
        Vector2 diff = this.sub(other);
        Vector2 vectDist = diff.mult(diff);
        return (float)Math.sqrt(vectDist.x + vectDist.y);
    }

    /**
     * Get the distance, to the nearest int, of this point from
     * another point, without scalar conversions
     * 
     * @param other The destination.
     * @return Distance to the other point.
     */
    public Vector2 pointDistTo(Vector2 other) {
        return this.sub(other);
    }

    /**
     * Get the distance, to the nearest int, of this point from
     * another point, without scalar conversions
     * 
     * @param other The destination.
     * @return Distance to the other point.
     */
    public Vector2 pointDistTo(Point other) {
        return this.sub(new Vector2(other));
    }

    /**
     * Convert the distance to a unit vector with a length of 1.
     * 
     * @return Normalized point.
     */
    public Vector2 normalize() {
        float len = this.distanceTo(Vector2.zero);
        return this.div(len);
    }

    /**
     * Approximate this point's value in integer pixel space.
     * 
     * @return Approximated values.
     */
    public Point approx() {
        return new Point(Math.round(this.x), Math.round(this.y));
    }

    /**
     * Print this object representation on screen.
     * 
     * @return This object's representation.
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
