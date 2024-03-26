package velocity.renderer;

import velocity.Rect;
import velocity.util.Point;

/**
 * As of Velocity v0.4.0.0, the standard input for rendering code
 * will be this. All calls to {@code render} will be changed to 
 * {@code render(DrawInfo d, FrameBuffer fb)}.
 */
public class DrawInfo {
    public final Rect drawRect;
    public final float rot;
    public final Point scale;
    public final int drawLayer;

    /**
     * Create the draw info.
     * 
     * @param pRect The sprite's rect transformed to screen space.
     * @param rot The sprite's rotation on screen.
     * @param scale The sprite's scale.
     * @param drawLayer The sorting layer to draw this sprite on.
     */
    public DrawInfo(Rect pRect, float rot, Point scale, int drawLayer) {
        this.drawRect = pRect;
        this.rot = rot;
        this.scale = scale;
        this.drawLayer = drawLayer;
    }
}
