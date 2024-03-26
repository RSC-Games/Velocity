package velocity;

import velocity.util.*;

public class Line {
    private Point start;
    private Point end;
    private float m;
    private float b;
    private boolean isVert;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
        updateMB();
    }

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

    private float findSlope() {
        // m = (p2.y-p1.y)/(p2.x-p1.x)
        return (float)(end.y - start.y) / (end.x - start.x);
    }

    private float findIntercept() {
        // b = p1.y - m * p1.x
        return start.y - (this.m * start.x);
    }

    private float calcPoint(float x) {
        return this.m * x + this.b;
    }

    // For a vertical line, will return -dist if on the left (negative) side of the line.
    // Intersecting is 0;
    // For a horizontal line (default), will return -dist if below the line and +dist if
    // above the line. Intersecting is 0;
    private float getSide(Point p) {
        if (isVert) 
            return p.x - this.start.x;
        return p.y - calcPoint(p.x);
    }

    private int getSign(float f) {
        if (f == 0) return 0;
        return f < 0 ? -1 : 1;
    }

    // Point culling. For horizontal lines (slope < 1), x-based point culling will
    // occur. For vertical lines (slope >= 1), y-based culling will occur.
    // For a vertical line we will also use y-based culling.
    private boolean pointInRange(Point p) {
        if (Math.abs(this.m) >= 1f || isVert)
            return !(p.y < Math.min(start.y, end.y) || p.y >= Math.max(start.y, end.y)); // Y-based
        return !(p.x < Math.min(start.x, end.x) || p.x >= Math.max(start.x, end.x) ); // X-based
    }

    public Point getStart() {
        return this.start;
    }

    public Point getEnd() {
        return this.end;
    }

    public boolean overlaps(Line other) {
        // I don't know how yet
        return false;
    }

    // Implement line collision too so you can't go through the map boundary line.
    // To do this, you need to see if the rectangle's 4 points are on the same side
    // of the line.
    // Implement in the Collidable object.
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
            System.out.println("Warning! Cannot determine collision geometry from 1 point!");

        for (int i = 1; i < ir; i++) {
            if (getSign(distances[i]) != reqSign) {
                //System.out.println("rect overlaps! *************************************************");
                return true;
            }
        }
        return false;
    }
}
