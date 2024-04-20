package velocity.sprite.ui;

import velocity.sprite.Camera;
import velocity.util.Point;

/**
 * Anchor points for UI and HUD rendering.
 */
public class AnchorPoint {
    /**
     * Calculate a base offset to transform an image from. The offset will change
     * whenever the screen is rescaled, so call this regularly.
     * 
     * @param loc A location on the UI canvas to anchor to.
     *   Can be one of the following values.
     *   "topleft": Anchor to the top left of the screen. Really not needed, since
     *      its the default.
     *   "top": Anchor to the top of the screen in the center on the x axis.
     *   "topright": Anchor to the top right corner of the screen.
     *   "right": Anchor to the right side of the screen in the center on the y axis.
     *   "left": Anchor to the left side of the screen in the center on the y axis.
     *   "bottomleft": Anchor to the bottom left corner of the screen.
     *   "bottom": Anchor to the bottom of the screen in the center on the x axis.
     *   "bottomright": Anchor to the bottom right corner of the screen.
     *   "center": Anchor to the center of the screen on both axis.
     * @return the actual anchor point in screen space.
     */
    public static Point getAnchor(String loc) {
        Point cres = Camera.res;

        switch (loc) {
            case "topleft":
                return new Point(0, 0);
            case "top":
                return new Point(cres.x / 2, 0);
            case "topright":
                return new Point(cres.x, 0);
            case "right":
                return new Point(cres.x, cres.y / 2);
            case "left":
                return new Point(0, cres.y / 2);
            case "bottomleft":
                return new Point(0, cres.y);
            case "bottom":
                return new Point(cres.x / 2, cres.y);
            case "bottomright":
                return cres;
            case "center":
                return cres.div(new Point(2, 2));
            default:
                throw new IllegalArgumentException("Invalid anchor point specified");
        }
    }
}
