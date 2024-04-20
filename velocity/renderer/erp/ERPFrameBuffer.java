package velocity.renderer.erp;

import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import velocity.Rect;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.renderer.RendererImage;
import velocity.util.Point;

/**
 * The Embedded Render Pipeline's framebuffer representation.
 */
public class ERPFrameBuffer implements FrameBuffer {
    /**
     * Enable the alpha channel on the framebuffer (for UI).
     */
    private boolean useAlpha;

    /**
     * The internal framebuffer data structure.
     */
    private BufferedImage b;

    /**
     * The framebuffer rectangle.
     */
    private Rect r;

    /**
     * The graphics system for operating on the framebuffer.
     */
    private Graphics2D g;

    /**
     * Create a framebuffer with the specified width and height.
     * 
     * @param w Width (in pixels).
     * @param h Height (in pixels).
     */
    public ERPFrameBuffer(int w, int h) {
        this(w, h, false);
    }

    /**
     * Create a framebuffer with the specified width and height. The alpha
     * channel may be enabled as well. 
     * 
     * @param w Width (in pixels).
     * @param h Height (in pixels).
     * @param useAlpha Enable the alpha channel in the frame buffer.
     */
    @SuppressWarnings("deprecation")
    public ERPFrameBuffer(int w, int h, boolean useAlpha) {
        if (w <= 0) w = 1;
        if (h <= 0) h = 1;

        this.b = new BufferedImage(w, h, 
            useAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
        this.g = (Graphics2D)b.getGraphics();
        this.g.setBackground(useAlpha ? new Color(0, 0, 0, 0) : Color.BLACK);

        int[] sRect = {0, 0, w, h};
        this.r = new Rect(sRect, true);
        this.useAlpha = useAlpha;
    }

    /**
     * Copy another framebuffer's data to this one.
     * 
     * @param other The other framebuffer to blit.
     * @param p The point to start blitting from.
     */
    public void blit(ERPFrameBuffer other, Point p) {
        this.blit0(other.b, p);
    }

    /**
     * Raw buffered images are no longer allowed to be blitted as-is due to potential
     * cross-renderer compatibility issues.
     * 
     * @param other The other raster to blit to this framebuffer.
     * @param p The location to draw it at.
     */
    private void blit0(BufferedImage other, Point p) {
        this.g.drawImage(other, p.x, p.y, null);
    }

    /** 
     * Standard issue blit. Clips pixel copy to framebuffer bounds and vastly
     * speeds up blit times with an optimized copy. Only supports TYPE_3BYTE_BGR
     * or TYPE_4BYTE_ABGR.
     * 
     * @param other The image to draw on this framebuffer.
     * @param d The drawing location and transform.
     */
    @Override
    public void blit(RendererImage other, DrawInfo d) {
        if (cullable(other, d)) return;

        Point p = d.drawRect.getDrawLoc();
        BufferedImage img = other.getTexture();
        this.g.drawImage(img, p.x, p.y, null);
    }

    /**
     * Standard issue blit. Clips pixel copy to framebuffer bounds and vastly
     * speeds up blit times with an optimized copy. Only supports TYPE_3BYTE_BGR
     * or TYPE_4BYTE_ABGR.
     * 
     * @param other The image to draw on this framebuffer.
     * @param d The drawing location and transform.
     */
    public void drawShaded(RendererImage other, DrawInfo d) {
        // ERP doesn't support lighting. Just bounce the call to blit.
        blit(other, d);
    }

    /**
     * Identify whether the provided object is cullable.
     * 
     * @param other RendererImage to draw.
     * @param d Draw info.
     * @return If its cullable.
     */
    private boolean cullable(RendererImage other, DrawInfo d) {
        // Don't waste time trying to draw a null image.
        if (other == null)
            return true;

        // RendererImage is used across many renderers. To avoid issues, we require
        // the type meant for this renderer.        
        if (!(other instanceof ERPRendererImage))
            throw new IllegalArgumentException("LV (CPU backend) cannot use generic RendererImage!");

        // Attempt to cull the texture if offscreen.
        if (!this.r.overlaps(d.drawRect))
            return true;

        return false;
    }

    // Unimplemented. Requires an array of generated images and uses z-buffering
    // to render them.
    void blitz() {}

    /**
     * Draw a given line on the screen in camera space.
     * 
     * @param p1 Starting point of the line.
     * @param p2 Ending point of the line.
     * @param weight Line width.
     * @param c Line draw color
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void drawText(Point pos, String text, Font font, Color c, int layer) {
        this.g.setFont(font);
        this.g.setColor(c);
        this.g.drawString(text, pos.x, pos.y);
    }

    @Deprecated(forRemoval=true)
    public BufferedImage DEBUG_getBufferedImage() {
        //Warnings.warn("Detected DEBUG_getBufferedImage() call");
        return this.b;
    }

    /**
     * Make a copy of this framebuffer.
     * 
     * @return A full copy of this buffer.
     */
    public ERPFrameBuffer copy() {
        ERPFrameBuffer temp = new ERPFrameBuffer(r.getW(), r.getH(), this.useAlpha);
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
     * Return the graphics subsystem for this framebuffer.
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
 