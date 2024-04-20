package velocity.renderer.debug;

import java.awt.event.*;
import java.util.ArrayList;

/**
 * Useful for trapping keyboard and mouse inputs but inflexible and duplicated code.
 * Input system code copied from the InputSystem code from Velocity v0.1.0.0
 */
public class DebugInput implements KeyListener {
    /**
     * Keys pressed during a frame.
     */
    private static ArrayList<Integer> keysDown = new ArrayList<Integer>();

    /**
     * Keys released during a frame.
     */
    private static ArrayList<Integer> keysUp = new ArrayList<Integer>();

    /**
     * Keys currently down.
     */
    private static ArrayList<Integer> keysActive = new ArrayList<Integer>();

    public void actionPerformed(ActionEvent e) {}
    public void keyTyped(KeyEvent e) {}
 
    /**
     * Called once per frame by the FrameRedrawListener. Clears the per-frame
     * key buffers.
     */
    public static void clearKeyBuffers() {
        keysDown.clear();
        keysUp.clear();
    }

    /**
     * Check if a key has been pressed.
     * 
     * @param keyCode The key code (from AWT's KeyEvent.VK_xxxx)
     * @return The key state.
     */
    public static boolean getKey(int keyCode) {
        return keysActive.contains(keyCode);
    }

    /**
     * Return two key presses on an axis between 1 to -1. Both keys should
     * be specified with an AWT KeyEvent.VK_xxxx.
     * 
     * @param pos Positive key press (pressed = 1, released = 0)
     * @param neg Negative key press (pressed = -1, released = 0)
     * @return The axis value.
     */
    public static int getAxis(int pos, int neg) {
        return (getKey(pos) ? 1 : 0) - (getKey(neg) ? 1 : 0);
    }

    /**
     * Get a key release event.
     * 
     * @param keyCode AWT KeyEvent.VK_xxxx key code.
     * @return The key state.
     */
    public static boolean getKeyUp(int keyCode) {
        return keysUp.contains(keyCode);
    }

    /**
     * AWT Event handler.
     * 
     * @param e Event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysDown.add(keyCode);

        if (!keysActive.contains(keyCode))
            keysActive.add(keyCode);
    }

    /**
     * AWT Event handler.
     * 
     * @param e Event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysUp.add(keyCode);
        keysActive.remove((Integer)keyCode);
    }
}
