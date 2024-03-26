package velocity.renderer.window;

import velocity.util.Point;

/**
 * VXRA's generic window system interface. GLFW / DirectX / AWT or any other frame type
 * may be contained within this class. Actual implementation must be done by each 
 * VXRA-compliant extension renderer.
 */
public interface Window {
    /**
     * Return this window's current resolution. May change based on previously issued
     * resize events.
     * 
     * @return Window resolution.
     */
    public Point getResolution();

    /**
     * Returns this window's current position on screen. May change if the player moves
     * the window.
     * 
     * @return Current window position on screen.
     */
    public Point getPosition();

    /**
     * Set the window's visibility on screen. Initially is not visible.
     * 
     * @param state Show window (if {@code true}) or hide it (if {@code false}).
     */
    public void setVisible(boolean state);
}
