package com.rsc_games.copperheadgl;

import java.awt.Color;
import com.rsc_games.velocity.util.Point;
import com.rsc_games.velocity.renderer.DrawInfo;
import com.rsc_games.velocity.renderer.FrameBuffer;
import com.rsc_games.velocity.renderer.RendererImage;

import java.awt.image.BufferedImage;

import com.rsc_games.velocity.Rect;

import java.awt.Graphics;
import java.awt.Font;

/**
 * Standard OpenGL Framebuffer representation. Always points and draws to the
 * backbuffer via the {@code GLRendererContext} draw queue.
 */
class GLFrameBuffer implements FrameBuffer {
    BufferedImage __img = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
    GLDrawQueue drawQueue;
    Rect fbRect;

    /**
     * Unknown what operations will go on here.
     * 
     * @param x Screen width
     * @param y Screen height
     */
    @SuppressWarnings("deprecation")
    public GLFrameBuffer(int x, int y) {
        this.drawQueue = new GLDrawQueue();
        this.fbRect = new Rect(new int[] {0, 0, x, y}, true);
    }

    /**
     * Get the local FrameBuffer Graphics object.
     * @deprecated Since DX11 and OGL do not support java.awt.Graphics,
     * framebuffer access must be faked.
     * 
     * @return framebuffer Graphics object.
    */
    @Deprecated(since="v0.2.1.0", forRemoval=true)
    public Graphics getGraphics() {
        return this.__img.getGraphics();
    }

    /**
     * NOTE: May be renamed to toBufferedImage(), which can be used across multiple
     * renderers.
     * Get the local FrameBuffer BufferedImage panel.
     * @deprecated Since this is specific to LumaViper CPU, it cannot be used
     * as an effective cross-renderer function.
     * 
     * @return framebuffer BufferedImage pixel storage.
    */
    @Deprecated(since="v0.2.1.0", forRemoval=true)
    public BufferedImage DEBUG_getBufferedImage() {
        return this.__img;
    }
    
    /**
     * Draw a static unlit and unshaded object to the framebuffer in screen
     * space. See {@code blit(RendererImage, Point, float)}.
     * 
     * @param img Sprite image reference.
     * @param d Sprite positioning and transform in screen space.
     */
    @Override
    public void blit(RendererImage img, DrawInfo d) {
        if (cullable(img, d)) return;

        // Issue the drawcall.
        // Not possible to eliminate the drawcall system due to the quirks of
        // VXRA.
        Object[] params = new Object[] {img, d};
        GLDrawCall drawCall = new GLDrawCall(GLCallType.DRAW_BLIT, params);
        drawQueue.pushCall(d.drawLayer, drawCall);
    }

    /**
     * Draw a dynamic shaded object to the framebuffer in screen
     * space.
     * 
     * @param img Sprite image reference.
     * @param d Sprite drawing information.
     */
    @Override
    public void drawShaded(RendererImage img, DrawInfo d) {
        if (cullable(img, d)) return;

        // Issue the drawcall.
        Object[] params = new Object[] {img, d};
        GLDrawCall drawcall = new GLDrawCall(GLCallType.DRAW_SHADE, params);
        drawQueue.pushCall(d.drawLayer, drawcall);
    }

    /**
     * Identify whether the provided object is cullable.
     * 
     * @param other RendererImage to draw.
     * @param d Draw info.
     * @return If its cullable.
     */
    @SuppressWarnings("deprecation")
    private boolean cullable(RendererImage other, DrawInfo d) {
        // Don't waste time trying to draw a null image.
        if (other == null)
            return true;

        // RendererImage is used across many renderers. To avoid issues, we require
        // the type meant for this renderer.        
        if (!(other instanceof GLRendererImage))
            throw new IllegalArgumentException("CopperheadGL cannot use generic RendererImage!");

        // Attempt to cull the texture if offscreen.
        Point pos = d.drawRect.getDrawLoc();
        Rect imgRect = new Rect(new int[] {pos.x, pos.y, other.getWidth(), other.getHeight()}, true);
        if (!this.fbRect.overlaps(imgRect))
            return true;

        return false;
    }

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
    @Override
    public void drawLine(Point p1, Point p2, int weight, Color c) {
        drawLine(p1, p2, weight, c, 0);
    }

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
     * @param sortLayer Sorting layer.
     */
    @Override
    public void drawLine(Point p1, Point p2, int weight, Color c, int sortLayer) {
        Object[] params = new Object[] {p1, p2, weight, c};
        GLDrawCall drawcall = new GLDrawCall(GLCallType.DRAW_LINE, params);
        drawQueue.pushCall(sortLayer, drawcall);
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
     * @param sortLayer Sorting layer.
     */
    @Override
    public void drawLines(Point[] points, int weight, Color c, boolean closed, int sortLayer) {
        //throw new UnsupportedFrameBufferOperation("lvogl", "drawLines");
        Object[] params = new Object[] {points, weight, c, closed};
        GLDrawCall drawcall = new GLDrawCall(GLCallType.DRAW_LINES, params);
        drawQueue.pushCall(sortLayer, drawcall);
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
     * @param sortLayer Sorting layer.
     */
    @Override
    public void drawRect(Rect r, int weight, Color c, boolean filled, int sortLayer) {
        //throw new UnsupportedFrameBufferOperation("lvogl", "drawRect");
        Object[] params = new Object[] {r, weight, c, filled};
        GLDrawCall drawcall = new GLDrawCall(GLCallType.DRAW_RECT, params);
        drawQueue.pushCall(sortLayer, drawcall);
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
     * @param sortLayer Sorting layer.
     */
    @Override
    public void drawTriangle(Point p1, Point p2, Point p3, int weight, Color c, int sortLayer) {
        //throw new UnsupportedFrameBufferOperation("lvogl", "drawTriangle");
        Object[] params = new Object[] {p1, p2, p3, weight, c};
        GLDrawCall drawcall = new GLDrawCall(GLCallType.DRAW_TRI, params);
        drawQueue.pushCall(sortLayer, drawcall);
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
     * @param sortLayer Sorting layer.
     */
    @Override
    public void drawCircle(Point center, int r, int weight, Color c, boolean filled, int sortLayer) {
        //throw new UnsupportedFrameBufferOperation("lvogl", "drawCircle");
        Object[] params = new Object[] {center, r, weight, c, filled};
        GLDrawCall drawcall = new GLDrawCall(GLCallType.DRAW_CIRCLE, params);
        drawQueue.pushCall(sortLayer, drawcall);
    }

    /**
     * Draw text on screen of a given font size.
     * 
     * @param pos Text draw location.
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
     * @param pos Text draw location.
     * @param text Text to draw.
     * @param font Font to draw with.
     * @param c Text color.
     * @param sortLayer Sorting layer.
     */
    @Override
    public void drawText(Point pos, String text, Font font, Color c, int sortLayer) {
        Object[] params = new Object[] {pos, text, font, c};
        GLDrawCall drawcall = new GLDrawCall(GLCallType.DRAW_TEXT, params);
        drawQueue.pushCall(sortLayer, drawcall);
    }

    /**
     * Force a resize of the current framebuffer culling rect.
     * 
     * @param w Screen width.
     * @param h Screen height.
     */
    @SuppressWarnings("deprecation")
    public void resize(int w, int h) {
        this.fbRect = new Rect(new int[] {0, 0, w, h}, true);
    }

    /**
     * Returns a copy of this FrameBuffer's attributes and pixel data.
     * 
     * @return A complete copy of this FrameBuffer.
     */
    public FrameBuffer copy() {
        throw new UnsupportedOperationException("Cannot copy framebuffer!");
    }

    /** 
     * Get the active draw queue for this framebuffer.
     * 
     * @return the draw queue.
     */
    public GLDrawQueue getDrawQueue() {
        return this.drawQueue;
    }

    /**
     * Clear the draw queue.
     */
    public void flushDrawQueue() {
        this.drawQueue.clear();
    }
}
