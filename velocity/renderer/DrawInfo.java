package velocity.renderer;

import velocity.Rect;
import velocity.util.Point;

/**
 * As of Velocity v0.4.0.0, the standard input for rendering code will be this. 
 * All calls to {@code render} will be changed to 
 * {@code render(DrawInfo d, FrameBuffer fb)}.
 * 
 * The DrawInfo struct represents a transform and z-order for an object to be
 * rendered on screen.
 */
public class DrawInfo {
    /**
     * The draw location and rectangle. Allows conversion from a center point
     * to the topleft point required for drawing.
     */
    public final Rect drawRect;

    /**
     * Image rotation angle. As of Velocity v0.5.2.3, rotation is still unsupported
     * in most renderers.
     */
    public final float rot;

    /**
     * Image scaling. As of Velocity v0.5.2.3, scaling is unsupported across
     * all official renderers.
     */
    public final Point scale;

    /**
     * Z-sorting layer. Most renderers currently support reordering on the z axis.
     */
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
