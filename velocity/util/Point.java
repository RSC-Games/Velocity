package velocity.util;

/**
 * General point representation used across Velocity.
 */
public class Point {
    /**
     * Zero point.
     */
    public static final Point zero = new Point(0, 0);

    /**
     * All values initialized to 1.
     */
    public static final Point one = new Point(1, 1);

    /**
     * 2D point that points upward.
     */
    public static final Point up = new Point(0, -1);

    /**
     * 2D Point pointing downward.
     */
    public static final Point down = new Point(0, 1);

    /**
     * 2D Point pointing right.
     */
    public static final Point right = new Point(1, 0);

    /**
     * 2D Point pointing left.
     */
    public static final Point left = new Point(-1, 0);

    /**
     * X location in pixels.
     */
    public int x;

    /**
     * Y location in pixels.
     */
    public int y;

    /**
     * Create a point at the given location. Note that increased y values
     * go further down as they increase.
     * 
     * @param x X location
     * @param y Y location
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a point at the given location. Note that increased y values
     * go further down as they increase.
     * 
     * @param p Vector 2 to convert from.
     */
    public Point(Vector2 p) {
        this.x = (int)p.x;
        this.y = (int)p.y;
    }

    /**
     * Add this and another point together.
     * 
     * @param other The other point to add.
     * @return The new combined points.
     */
    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    /**
     * Add this point to a scalar and make a new one.
     * 
     * @param other The scalar value to add
     * @return The new point moved.
     */
    public Point add(int other) {
        return new Point(this.x + other, this.y + other);
    }

    /**
     * Subtract this point from another another point.
     * 
     * @param other The other point to subtract.
     * @return The new transformed points.
     */
    public Point sub(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    /**
     * Subtract this point to a scalar and make a new one.
     * 
     * @param other The scalar value to subtract
     * @return The new point transformed.
     */
    public Point sub(int other) {
        return new Point(this.x - other, this.y - other);
    }

    /**
     * Multiply two points together.
     * 
     * @param other The other point to multiply.
     * @return The new transformed points.
     */
    public Point mult(Point other) {
        return new Point(this.x * other.x, this.y * other.y);
    }

    /**
     * Multiply this point and a scalar together.
     * 
     * @param other The scalar to multiply.
     * @return The new transformed points.
     */
    public Point mult(int other) {
        return new Point(this.x * other, this.y * other);
    }

    /**
     * Divide this point by another.
     * 
     * @param other Other point to divide by.
     * @return The new transformed points.
     */
    public Point div(Point other) {
        return new Point(this.x / other.x, this.y / other.y);
    }

    /**
     * Divide this point by a scalar value.
     * 
     * @param other The scalar to divide by.
     * @return The new transformed points.
     */
    public Point div(int other) {
        return new Point(this.x / other, this.y / other);
    }

    /**
     * Get the modulus of this point.
     * 
     * @param other The other point.
     * @return The new transformed point.
     */
    public Point mod(Point other) {
        return new Point(this.x % other.x, this.y % other.y);
    }

    /**
     * Get the modulus of this point by a scalar.
     * 
     * @param other The scalar value.
     * @return The new transformed point.
     */
    public Point mod(int other) {
        return new Point(this.x % other, this.y % other);
    }

    /**
     * Get the distance, to the nearest int, of this point from
     * another point.
     * 
     * @param other The destination.
     * @return Distance to the other point.
     */
    public int distanceTo(Point other) {
        Point diff = this.sub(other);
        Point vectDist = diff.mult(diff);
        return (int)Math.sqrt(vectDist.x + vectDist.y);
    }

    /**
     * Get the distance, to the nearest int, of this point from
     * another point, without scalar conversions
     * 
     * @param other The destination.
     * @return Distance to the other point.
     */
    public Point pointDistTo(Point other) {
        return this.sub(other);
    }

    /**
     * Convert the distance to a unit vector with a length of 1.
     * 
     * @return Normalized point.
     */
    public Point normalize() {
        int len = this.distanceTo(Point.zero);
        return this.div(len);
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
