package velocity;

import velocity.util.*;

/**
 * Velocity's line representation. Represents a line at two distinct points.
 */
public class Line {
    /**
     * The starting location of the line.
     */
    private Point start;

    /**
     * The ending location of the line.
     */
    private Point end;

    /**
     * The currently cached slope.
     */
    private float m;

    /**
     * The currently cached y-intercept.
     */
    private float b;

    /**
     * Whether the line is more vertical than horizontal (and must be calculated differently).
     */
    private boolean isVert;

    /**
     * Create a line at two points.
     * 
     * @param start The start point.
     * @param end The end point.
     */
    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
        updateMB();
    }

    /**
     * Recalculate the slope and the y-intercept.
     */
    private void updateMB() {
        if (this.start.x == this.end.x) {
            isVert = true;
            return;
        }
        
        isVert = false;

        // Find the slope
        this.m = findSlope();
        this.b = findIntercept();
    }

    /**
     * Calculate the slope of this line.
     * 
     * @return The calculated slope.
     */
    private float findSlope() {
        // m = (p2.y-p1.y)/(p2.x-p1.x)
        return (float)(end.y - start.y) / (end.x - start.x);
    }

    /**
     * Find the y-intercept of this line.
     * 
     * @return The y intercept.
     */
    private float findIntercept() {
        // b = p1.y - m * p1.x
        return start.y - (this.m * start.x);
    }

    /**
     * Calculate a point along the line.
     * 
     * @param x The x position.
     * @return The approximated y location.
     */
    private float calcPoint(float x) {
        return this.m * x + this.b;
    }

    /**
     * For a vertical line, will return -dist if on the left (negative) side of the line.
     * Intersecting is 0;
     * For a horizontal line (default), will return -dist if below the line and +dist if
     * above the line. Intersecting is 0;
     * 
     * @param p The point to get the side from.
     * @return -distance below or +dist above.
     */
    private float getSide(Point p) {
        if (isVert) 
            return p.x - this.start.x;
        return p.y - calcPoint(p.x);
    }

    /**
     * Get the sign of a provided number.
     * 
     * @param f The number to extract the sign from.
     * @return The sign of the number.
     */
    private int getSign(float f) {
        if (f == 0) return 0;
        return f < 0 ? -1 : 1;
    }

    /**
     * Point culling. For horizontal lines (slope < 1), x-based point culling will
     * occur. For vertical lines (slope >= 1), y-based culling will occur.
     * For a vertical line we will also use y-based culling.
     * 
     * @param p The point to test.
     * @return Whether the point is close enough to the line to test collision.
     */
    private boolean pointInRange(Point p) {
        if (Math.abs(this.m) >= 1f || isVert)
            return !(p.y < Math.min(start.y, end.y) || p.y >= Math.max(start.y, end.y)); // Y-based
        return !(p.x < Math.min(start.x, end.x) || p.x >= Math.max(start.x, end.x) ); // X-based
    }

    /**
     * Get the starting point of this line.
     * 
     * @return The starting point.
     */
    public Point getStart() {
        return this.start;
    }

    /**
     * Get the ending point of the line.
     * 
     * @return The ending point.
     */
    public Point getEnd() {
        return this.end;
    }

    /**
     * Line-to-line collision is not currently supported. There have been zero
     * use cases so far. 
     * 
     * @param other The other line.
     * @return Whether they overlap.
     */
    public boolean overlaps(Line other) {
        // I don't know how yet
        return false;
    }

    /**
     * Implement line collision too so you can't go through the map boundary line.
     * To do this, you need to see if the rectangle's 4 points are on the same side
     * of the line.
     * 
     * @param other The other rect to test collision with.
     * @return Whether they overlap.
     */
    public boolean overlaps(Rect other) {
        Point pos = other.getDrawLoc();
        Point wh = new Point(other.getW(), other.getH());

        Point[] rPoints = new Point[] {
            pos,  // Top left
            new Point(pos.x + wh.x, pos.y),  // Top right
            new Point(pos.x, pos.y + wh.y),  // Bottom left
            pos.add(wh)  // Bottom right
        };

        boolean[] pInRange = new boolean[4];
        int inRange = 0;
        boolean anyTrue = false;
        for (int i = 0; i < 4; i++) {
            Point t = rPoints[i];
            if (pointInRange(t)) {
                anyTrue = true;
                pInRange[i] = true;
                inRange++;
            }
        }

        // Quick exit: don't process when other rect is out of line range.
        if (!anyTrue)
            return false;

        float[] distances = new float[inRange];
        int ir = 0;
        for (int i = 0; i < 4; i++) {
            if (!pInRange[i]) 
                continue;

            Point t = rPoints[i];
            distances[ir] = getSide(t);
            ir++;
        }

        int reqSign = getSign(distances[0]);

        // Really only for debugging.
        if (ir == 1)
            Logger.warn("velocity.Line","Cannot determine collision geometry from 1 point!");

        for (int i = 1; i < ir; i++) {
            if (getSign(distances[i]) != reqSign) {
                //System.out.println("rect overlaps! *************************************************");
                return true;
            }
        }
        return false;
    }
}
