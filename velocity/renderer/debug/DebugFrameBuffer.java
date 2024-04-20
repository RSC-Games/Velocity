package velocity.renderer.debug;

import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

//import util.Counter;
import velocity.Rect;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.util.Point;

/**
 * Frame Buffer implementation for the debug renderer.
 */
public class DebugFrameBuffer implements FrameBuffer {
    private BufferedImage b;
    private Rect r;
    private Graphics2D g;

    /**
     * Create a debug frame buffer.
     * 
     * @param w Debug FB width.
     * @param h Debug FB height.
     */
    @SuppressWarnings("deprecation")
    public DebugFrameBuffer(int w, int h) {
        if (w <= 0) w = 1;
        if (h <= 0) h = 1;

        this.b = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        this.g = (Graphics2D)b.getGraphics();
        this.g.setBackground(Color.BLACK);

        int[] sRect = {0, 0, w, h};
        this.r = new Rect(sRect, true);
    }

    /**
     * Blit another framebuffer to this one.
     * 
     * @param other The other framebuffer to write.
     * @param p The offset to draw it at.
     */
    public void blit(DebugFrameBuffer other, Point p) {
        this.blit0(other.b, p);
    }

    /**
     * Raw buffered images are no longer allowed to be blitted as-is due to potential
     * cross-renderer compatibility issues, but are still required for frame buffer
     * blits. 
     * 
     * @param other Other image to write.
     * @param p The offset to draw it at.
     */
    private void blit0(BufferedImage other, Point p) {
        this.g.drawImage(other, p.x, p.y, null);
    }

    /** 
     * Standard issue blit. Clips pixel copy to framebuffer bounds and vastly
     * speeds up blit times with an optimized copy. Only supports TYPE_3BYTE_BGR
     * or TYPE_4BYTE_ABGR.
     * 
     * @param other A renderer image to draw on screen.
     * @param d The location info for drawing.
     */
    @Override
    public void blit(RendererImage other, DrawInfo d) {
        if (other == null) return;

        Point p = d.drawRect.getDrawLoc();
        BufferedImage img = other.getTexture();
        this.g.drawImage(img, p.x, p.y, null);
    }

    /** 
     * Standard issue blit. Clips pixel copy to framebuffer bounds and vastly
     * speeds up blit times with an optimized copy. Only supports TYPE_3BYTE_BGR
     * or TYPE_4BYTE_ABGR.
     * 
     * @param other A renderer image to draw on screen.
     * @param d The location info for drawing.
     */
    @Override
    public void drawShaded(RendererImage other, DrawInfo d) {
        // ERP doesn't support lighting. Just bounce the call to blit.
        blit(other, d);
    }

    /**
     * Unimplemented. Requires an array of generated images and uses z-buffering
     * to render them.
     */
    void blitz() {}

    /**
     * Draw a given line on the screen in camera space.
     * 
     * @param p1 Starting point of the line.
     * @param p2 Ending point of the line.
     * @param weight Line width.
     * @param c Line draw color
     */
    public void drawLine(Point p1, Point p2, int weight, Color c) {
        drawLine(p1, p2, weight, c, 0);
    }

    /**
     * Draw a given line on the screen in camera space.
     * 
     * @param p1 Starting point of the line.
     * @param p2 Ending point of the line.
     * @param weight Line width.
     * @param c Line draw color
     * @param layer Sort layer.
     */
    public void drawLine(Point p1, Point p2, int weight, Color c, int layer) {
        this.g.setColor(c);
        this.g.setStroke(new BasicStroke(weight));
        this.g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Draw given lines in order with the given array of points.
     * 
     * @param points All points to draw as lines on-screen.
     * @param weight Line width in pixels.
     * @param c Line color
     * @param closed Whether or not to join the last and first points to complete the shape.
     */
    public void drawLines(Point[] points, int weight, Color c, boolean closed) {
        drawLines(points, weight, c, closed, 0);
    }

    /**
     * Draw given lines in order with the given array of points.
     * 
     * @param points All points to draw as lines on-screen.
     * @param weight Line width in pixels.
     * @param c Line color
     * @param closed Whether or not to join the last and first points to complete the shape.
     * @param layer Sorting layer.
     */
    public void drawLines(Point[] points, int weight, Color c, boolean closed, int layer) {
        this.g.setColor(c);
        this.g.setStroke(new BasicStroke(weight));

        // Build the render point arrays
        int pointCnt = points.length;
        int[] xPoints = new int[pointCnt];
        int[] yPoints = new int[pointCnt];
        Arrays.parallelSetAll(xPoints, i->points[i].x);
        Arrays.parallelSetAll(yPoints, i->points[i].y);

        // Draw the polygon.
        this.g.drawPolygon(xPoints, yPoints, pointCnt);
    }

    /**
     * Draw a rectangle with the given {@code rect}.
     * 
     * @param r Rectangle location and width.
     * @param weight Rectangle line width.
     * @param c Rectangle color
     * @param filled Whether or not to fill the rectangle after drawing it.
     */
    public void drawRect(Rect r, int weight, Color c, boolean filled) {
        drawRect(r, weight, c, filled, 0);
    }

    /**
     * Draw a rectangle with the given {@code rect}.
     * 
     * @param r Rectangle location and width.
     * @param weight Rectangle line width.
     * @param c Rectangle color
     * @param filled Whether or not to fill the rectangle after drawing it.
     * @param layer Sorting layer.
     */
    public void drawRect(Rect r, int weight, Color c, boolean filled, int layer) {
        this.g.setColor(c);
        this.g.setStroke(new BasicStroke(weight));
        Point pos = r.getDrawLoc();

        if (filled)
            this.g.fillRect(pos.x, pos.y, r.getW(), r.getH());
        else
            this.g.drawRect(pos.x, pos.y, r.getW(), r.getH());
    }

    /**
     * Draw a triangle at 3 points.
     * 
     * @param p1 1st point
     * @param p2 2nd point
     * @param p3 3rd point
     * @param weight Line width
     * @param c Color of lines to draw.
     */
    public void drawTriangle(Point p1, Point p2, Point p3, int weight, Color c) {
        drawTriangle(p1, p2, p3, weight, c, 0);
    }

    /**
     * Draw a triangle at 3 points.
     * 
     * @param p1 1st point
     * @param p2 2nd point
     * @param p3 3rd point
     * @param weight Line width
     * @param c Color of lines to draw.
     * @param layer Sorting layer.
     */
    public void drawTriangle(Point p1, Point p2, Point p3, int weight, Color c, int layer) {
        this.g.setColor(c);
        this.g.setStroke(new BasicStroke(weight));
        this.drawLines(new Point[] {p1, p2, p3}, weight, c, true);
    }

    /**
     * Draw a circle with an arbitrary radius.
     * 
     * @param center Circle center point.
     * @param r Circle radius
     * @param weight Line width
     * @param c Circle color
     * @param filled Whether to fill the circle or not.
     */
    public void drawCircle(Point center, int r, int weight, Color c, boolean filled) {
        drawCircle(center, r, weight, c, filled, 0);
    }

    /**
     * Draw a circle with an arbitrary radius.
     * 
     * @param center Circle center point.
     * @param r Circle radius
     * @param weight Line width
     * @param c Circle color
     * @param filled Whether to fill the circle or not.
     * @param layer Sorting layer.
     */
    public void drawCircle(Point center, int r, int weight, Color c, boolean filled, int layer) {
        this.g.setColor(c);
        this.g.setStroke(new BasicStroke(weight));

        if (filled)
            this.g.fillArc(center.x, center.y, r, r, 0, 360);
        else
            this.g.drawArc(center.x, center.y, r, r, 0, 360);
    }
    
    /**
     * Draw text on screen of a given font size.
     * 
     * @param pos Text draw location (top left)
     * @param text Text to draw.
     * @param font Font to draw with.
     * @param c Text color.
     */
    public void drawText(Point pos, String text, Font font, Color c) {
        drawText(pos, text, font, c, 0);
    }

    /**
     * Draw text on screen of a given font size.
     * 
     * @param pos Text draw location (top left)
     * @param text Text to draw.
     * @param font Font to draw with.
     * @param c Text color.
     * @param layer Sorting layer.
     */
    public void drawText(Point pos, String text, Font font, Color c, int layer) {
        this.g.setFont(font);
        this.g.setColor(c);
        this.g.drawString(text, pos.x, pos.y);
    }

    /**
     * @deprecated Getbufferedimage is non-flexible.
     */
    @Deprecated()
    public BufferedImage DEBUG_getBufferedImage() {
        throw new RuntimeException("Unsupported call: DEBUG_getBufferedImage()");
    }

    /**
     * Copy this framebuffer.
     */
    @Override
    public DebugFrameBuffer copy() {
        DebugFrameBuffer temp = new DebugFrameBuffer(r.getW(), r.getH());
        temp.blit(this, new Point(0, 0));
        return temp;
    }

    /**
     * AWT-specific. Writes this framebuffer to a provided screen buffer.
     * 
     * @param g Graphics object to copy to.
     */
    public void blitTo(Graphics g) {
        g.drawImage(this.b, 0, 0, null);
    }

    /**
     * Get this framebuffer's graphics implementation.
     * 
     * @return The graphics pointer.
     */
    @Deprecated
    public Graphics getGraphics() {
        return this.b.getGraphics();
    }
    
    /**
     * Erase this framebuffer.
     */
    public void clear() {
        this.g.clearRect(0, 0, this.r.getW(), this.r.getH());
    }
}
 