package velocity;

import velocity.util.Point;
import velocity.util.Warnings;

/**
 * Standard Velocity rectangle representation. Only allows integer coordinates and
 * values for size and position.
 */
public class Rect {
    /**
     * This rectangle's top left X position (in pixels).
     */
    private int x;

    /**
     * This rectangle's top left Y position (in pixels).
     */
    private int y;

    /**
     * This rectangle's width (in pixels).
     */
    private int w;

    /**
     * This rectangle's height (in pixels).
     */
    private int h;

    /**
     * Externally visible X location (center location).
     * Externally, if the location of the object is requested, the center coordinates
     * will be given.
     */
    private int centerX;

    /**
     * Externally visibly Y location (center location).
     */
    private int centerY;

    /**
     * Create a rectangle at the center coordinates cx, cy with the
     * width and height w, h.
     * 
     * @param cx Center X location (in pixels).
     * @param cy Center Y location (in pixels).
     * @param w Width (in pixels).
     * @param h Height (in pixels).
     */
    public Rect(int cx, int cy, int w, int h) {
        this.w = w;
        this.h = h;
        this.setPos(new Point(cx, cy));
    }

    /**
     * Create a rectangle at the center coordinates p, with the width and
     * height w, h.
     * 
     * @param p Center position (in pixels).
     * @param w Width of rect (in pixels).
     * @param h Height of rect (in pixels).
     */
    public Rect(Point p, int w, int h) {
        this.w = w;
        this.h = h;
        this.setPos(p);
    }

    /**
     * Create a rectangle at the center coordinates p, with the width and
     * height wh.
     * 
     * @param p Center position (in pixels).
     * @param wh Width and height (in pixels).
     */
    public Rect(Point p, Point wh) {
        this.w = wh.x;
        this.h = wh.y;
        this.setPos(p);
    }

    /**
     * @deprecated For Velocity Internal use only. Does not set the center x or y
     * locations. Only used for hard-setting the topleft position and width.
     * 
     * @param pos Array of values for setting.
     * @param suppressWarning Velocity will automatically warn when this constructor is
     *  used. Prevent that.
     */
    @Deprecated(forRemoval=false, since="v0.4.0.1")
    public Rect(int[] pos, boolean suppressWarning) {
        if (!suppressWarning)
            Warnings.warn("velocity.Rect", 
                "Rect(int[], boolean) doesn't set all required values!");
        this.x = pos[0];
        this.y = pos[1];
        this.w = pos[2];
        this.h = pos[3];
        this.centerX = 0;
        this.centerY = 0;
    }

    /**
     * Set the rectangle's width and height.
     * 
     * @param w Rectangle width.
     * @param h Rectangle height.
     */
    public void setWH(int w, int h) {
        this.w = w;
        this.h = h;
        this.setPos(new Point(centerX, centerY));
    }

    /**
     * Set the rectangle's width and height.
     * 
     * @param wh Width and Height of the rectangle.
     */
    public void setWH(Point wh) {
        this.w = wh.x;
        this.h = wh.y;
        this.setPos(new Point(centerX, centerY));
    }

    /**
     * Get this rectangle's width.
     * 
     * @return Width.
     */
    public int getW() {
        return this.w;
    }

    /**
     * Get this rectangle's height.
     * 
     * @return Height.
     */
    public int getH() {
        return this.h;
    }
    
    /**
     * Get this rectangle's size.
     * 
     * @return The rectangle width and height.
     */
    public Point getWH() {
        return new Point(this.w, this.h);
    }

    /**
     * Move this rect a given distance.
     * 
     * @param p Move distance.
     */
    public void translate(Point p) {
        this.setPos(this.getPos().add(p));
    }

    @Deprecated
    public int[] getPixelPos() {
        throw new UnsupportedOperationException("Function obsolete!");
    }

    /**
     * Replaces {@code getPixelPos()}. Provides the top left point of this
     * rect. Commonly required for drawing, hence the name {@code getDrawLoc()}.
     * 
     * @return This rect's top left corner location.
     */
    public Point getDrawLoc() {
        return new Point(this.x, this.y);
    }

    /**
     * Get the current center location of this rectangle.
     * 
     * @return Center location (in pixels).
     */
    public Point getPos() {
        return new Point(this.centerX, this.centerY);
    }

    /**
     * Set the current center location of this rect.
     * Automatically regenerates inner values as needed.
     * 
     * @param p New center.
     */
    public void setPos(Point p) {
        this.centerX = p.x;
        this.centerY = p.y;

        // Maybe round later for slightly higher precision?
        this.x = this.centerX - (this.w / 2);
        this.y = this.centerY - (this.h / 2);
    }

    /**
     * Duplicate this rect.
     * 
     * @return A copied version of this rect.
     */
    public Rect copy() {
        return new Rect(this.centerX, this.centerY, this.w, this.h);
    }

    /**
     * Collision detection. Detects any overlap on this rect and another
     * passed-in rect.
     * 
     * @param other Other rect to collide with.
     * @return If they overlap.
     */
    public boolean overlaps(Rect other) {
        int tr = this.x + this.w;
        int tl = this.x;
        int tt = this.y;
        int tb = this.y + this.h;

        int or = other.x + other.w;
        int ol = other.x;
        int ot = other.y;
        int ob = other.y + other.h;
        
        /*
        (RectA.Left < RectB.Right 
        && RectA.Right > RectB.Left &&
         RectA.Top > RectB.Bottom && 
         RectA.Bottom < RectB.Top )
        */

        return (tl < or  // This right edge is less than the left edge of the other collider.
            && tr > ol // This left edge is greater than the right edge of the other.
            && (tt < ob // The top is lower than the bottom of the other.
            && tb > ot) // The bottom is higher than the top.
        );
    }
    
    /**
     * Print this rect's internal information out for debug purposes.
     * 
     * @return This representation.
     */
    public String toString() {
        return "(" + x + ", " + y + ", " + w + ", " + h + ") (cx: " + centerX + ", cy: " + centerY + ")";
    }
}
