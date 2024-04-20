package velocity.renderer;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import velocity.Rect;
import velocity.util.Point;

/**
 * A multi-renderer agnostic framebuffer implementation that allows for
 * easy platform-specific abstraction at the game code level.
 */
public interface FrameBuffer {
    /**
     * Get the local FrameBuffer Graphics object.
     * @deprecated Since DX11 and OGL do not support java.awt.Graphics,
     * another object must be provided.
     * 
     * @return framebuffer Graphics object.
    */
    @Deprecated()
    public Graphics getGraphics();

    /**
     * NOTE: May be renamed to toBufferedImage(), which can be used across multiple
     * renderers.
     * Get the local FrameBuffer BufferedImage panel.
     * @deprecated Since this is specific to LumaViper CPU, it cannot be used
     * as an effective cross-renderer function.
     * 
     * @return framebuffer BufferedImage pixel storage.
    */
    @Deprecated(since="v0.2.0.0", forRemoval=true)
    public BufferedImage DEBUG_getBufferedImage();
    


    /**
     * Draw a static unlit and unshaded object to the framebuffer in screen
     * space. See {@code blit(RendererImage, Point, float)}.
     * 
     * @param img Sprite image reference.
     * @param d Draw parameters for rendering.
     */
    public void blit(RendererImage img, DrawInfo d);

    /**
     * Draw a dynamic shaded object to the framebuffer in screen
     * space.
     * 
     * @param img Sprite image reference.
     * @param d Drawing information (with pos in screen space).
     */
    public void drawShaded(RendererImage img, DrawInfo d);



    /**
     * All extension renderers must implement shape-drawing code. Primitives are
     * the backbone of many cool effects and are especially important in OpenGL.
     * A basic line renderer is a required feature.
     * 
     * Draw a given line on the screen in camera space.
     * 
     * @param p1 Starting point of the line.
     * @param p2 Ending point of the line.
     * @param weight Line width.
     * @param c Line draw color
     */
    public abstract void drawLine(Point p1, Point p2, int weight, Color c);

    /**
     * All extension renderers must implement shape-drawing code. Primitives are
     * the backbone of many cool effects and are especially important in OpenGL.
     * A basic line renderer is a required feature.
     * 
     * Draw a given line on the screen in camera space.
     * 
     * @param p1 Starting point of the line.
     * @param p2 Ending point of the line.
     * @param weight Line width.
     * @param c Line draw color
     * @param sortLayer Drawing layer.
     */
    public abstract void drawLine(Point p1, Point p2, int weight, Color c, int sortLayer);

    /**
     * Draw given lines in order with the given array of points.
     * 
     * @param points All points to draw as lines on-screen.
     * @param weight Line width in pixels.
     * @param c Line color
     * @param closed Whether or not to join the last and first points to complete the shape.
     */
    public abstract void drawLines(Point[] points, int weight, Color c, boolean closed);

    /**
     * Draw given lines in order with the given array of points.
     * 
     * @param points All points to draw as lines on-screen.
     * @param weight Line width in pixels.
     * @param c Line color
     * @param closed Whether or not to join the last and first points to complete the shape.
     * @param sortLayer Sorting layer to draw on.
     */
    public abstract void drawLines(Point[] points, int weight, Color c, 
                                   boolean closed, int sortLayer);

    /**
     * Draw a rectangle with the given {@code rect}.
     * 
     * @param r Rectangle location and width.
     * @param weight Rectangle line width.
     * @param c Rectangle color
     * @param filled Whether or not to fill the rectangle after drawing it.
     */
    public abstract void drawRect(Rect r, int weight, Color c, boolean filled);

    /**
     * Draw a rectangle with the given {@code rect}.
     * 
     * @param r Rectangle location and width.
     * @param weight Rectangle line width.
     * @param c Rectangle color
     * @param filled Whether or not to fill the rectangle after drawing it.
     * @param sortLayer Sorting layer to draw on.
     */
    public abstract void drawRect(Rect r, int weight, Color c, boolean filled, int sortLayer);

    /**
     * Draw a triangle at 3 points.
     * 
     * @param p1 1st point
     * @param p2 2nd point
     * @param p3 3rd point
     * @param weight Line width
     * @param c Color of lines to draw.
     */
    public abstract void drawTriangle(Point p1, Point p2, Point p3, int weight, Color c);

    /**
     * Draw a triangle at 3 points.
     * 
     * @param p1 1st point
     * @param p2 2nd point
     * @param p3 3rd point
     * @param weight Line width
     * @param c Color of lines to draw.
     * @param sortLayer Sorting layer.
     */
    public abstract void drawTriangle(Point p1, Point p2, Point p3, int weight, 
                                      Color c, int sortLayer);

    /**
     * Draw a circle with an arbitrary radius.
     * 
     * @param center Circle center point.
     * @param r Circle radius
     * @param weight Line width
     * @param c Circle color
     * @param filled Whether to fill the circle or not.
     */
    public abstract void drawCircle(Point center, int r, int weight, Color c, boolean filled);

    /**
     * Draw a circle with an arbitrary radius.
     * 
     * @param center Circle center point.
     * @param r Circle radius
     * @param weight Line width
     * @param c Circle color
     * @param filled Whether to fill the circle or not.
     * @param sortLayer Sorting layer of the shape.
     */
    public abstract void drawCircle(Point center, int r, int weight, Color c, 
                                    boolean filled, int sortLayer);

    /**
     * Draw text on screen of a given font size.
     * 
     * @param pos Text draw location.
     * @param text Text to draw.
     * @param font Provided font to draw with.
     * @param c Text color.
     */
    public abstract void drawText(Point pos, String text, Font font, Color c);

    /**
     * Draw text on screen of a given font size.
     * 
     * @param pos Text draw location.
     * @param text Text to draw.
     * @param font Provided font to draw with.
     * @param c Text color.
     * @param sortLayer Sorting layer to draw the text.
     */
    public abstract void drawText(Point pos, String text, Font font, Color c, int sortLayer);

    /**
     * Returns a copy of this FrameBuffer's attributes and pixel data.
     * 
     * @return A complete copy of this FrameBuffer.
     */
    public FrameBuffer copy();
}
