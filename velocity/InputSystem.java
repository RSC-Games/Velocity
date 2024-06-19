package velocity;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import velocity.renderer.window.Window;
import velocity.util.Point;

/**
 * Velocity's input system and HID event handler. Allows access to HID events
 * cleanly and effectively.
 */
public class InputSystem {
    /**
     * The current input system backend.
     */
    public static InputSystem inputSystemBackend;

    /** 
     * List of all keys pressed this frame.
     */
    private ArrayList<Integer> keysDown = new ArrayList<Integer>();

    /** 
     * List of all keys pressed this frame.
     */
    private ArrayList<Integer> keysDownBuffer = new ArrayList<Integer>();

    /**
     * List of all keys released this frame.
     */
    private ArrayList<Integer> keysUp = new ArrayList<Integer>();

    /**
     * Internal list of all keys released this frame.
     */
    private ArrayList<Integer> keysUpBuffer = new ArrayList<Integer>();

    /**
     * List of all currently pressed keys.
     */
    private ArrayList<Integer> keysActive = new ArrayList<Integer>();

    /**
     * List of all mouse buttons pressed this frame.
     */
    private ArrayList<Integer> mouseDown = new ArrayList<Integer>();

    /**
     * Internal list of newly pressed mouse buttons.
     */
    private ArrayList<Integer> mouseDownBuffer = new ArrayList<Integer>();

    /**
     * List of all mouse buttons released this frame.
     */
    private ArrayList<Integer> mouseUp = new ArrayList<Integer>();

    /**
     * Internal list of all mouse buttons released this frame.
     */
    private ArrayList<Integer> mouseUpBuffer = new ArrayList<Integer>();

    /**
     * List of all mouse buttons currently pressed.
     */
    private ArrayList<Integer> mouseActive = new ArrayList<Integer>();

    /**
     * Refresh all key data. Called by the renderer redraw event handler.
     */
    public void clearKeyBuffers() {
        keysDown.clear();
        keysUp.clear();
        mouseDown.clear();
        mouseUp.clear();

        publishKeyPresses();
    }

    /**
     * Update the currently visible keypresses/mouse presses with the cached ones.
     */
    private void publishKeyPresses() {
        keysDown.addAll(keysDownBuffer);
        keysDownBuffer.clear();
        keysUp.addAll(keysUpBuffer);
        keysUpBuffer.clear();

        mouseDown.addAll(mouseDownBuffer);
        mouseDownBuffer.clear();
        mouseUp.addAll(mouseUpBuffer);
        mouseUpBuffer.clear();
    }

    /**
     * Create a new input system
     */
    public static InputSystem createInputSystem() {
        if (inputSystemBackend != null)
            throw new IllegalStateException("Input system already exists!");
        
        inputSystemBackend = new InputSystem();
        return inputSystemBackend;
    }

    /**
     * Get whether a key is currently pressed.
     * 
     * @param keyCode The key scan code.
     * @return Whether the key was pressed.
     */
    public boolean getKey0(int keyCode) {
        return keysActive.contains(keyCode);
    }

    /**
     * Get a key state from a key code.
     * 
     * @param keyCode Key Code.
     * @return The key state (if its pressed).
     */
    public static boolean getKey(int keyCode) {
        return inputSystemBackend.getKey0(keyCode);
    }

    /**
     * Get whether a key was just pressed.
     * 
     * @param keyCode The key scan code.
     * @return Whether the key was just pressed.
     */
    public boolean getKeyDown0(int keyCode) {
        return keysDown.contains(keyCode);
    }

    /**
     * Get a key state from a key code.
     * 
     * @param keyCode Key Code.
     * @return The key state (if its pressed).
     */
    public static boolean getKeyDown(int keyCode) {
        return inputSystemBackend.getKeyDown0(keyCode);
    }

    /**
     * Get a int value between -1 and 1 from a provided axis.
     * 
     * @param pos Positive button.
     * @param neg Negative button.
     * @return The axis (from -1 to 1).
     */
    public static int getAxis(int pos, int neg) {
        return (inputSystemBackend.getKey0(pos) ? 1 : 0) 
                - (inputSystemBackend.getKey0(neg) ? 1 : 0);
    }

    /**
     * Get whether a key was released this frame.
     * 
     * @param keyCode The key id.
     * @return Whether it's been pressed or not.
     */
    public boolean getKeyUp0(int keyCode) {
        return keysUp.contains(keyCode);
    }

    /**
     * Get a released key.
     * 
     * @param keyCode Key code.
     * @return Whether it's been released.
     */
    public static boolean getKeyUp(int keyCode) {
        return inputSystemBackend.getKeyUp0(keyCode);
    }

    /**
     * Get the mouse location within the window.
     * 
     * @return Mouse location in the window.
     */
    public static Point getMousePos() {
        Window window = VXRA.rp.getWindow();
        return window.getPointerLocation();
    }

    /**
     * Test for a button press.
     * 
     * @param buttonID The mouse button id.
     * @return Whether it's been pressed.
     */
    public boolean clicked0(int buttonID) {
        return mouseActive.contains(buttonID);
    }

    /**
     * Get a mouse button pressed state.
     * 
     * @param buttonID Button id.
     * @return Whether it's pressed or not.
     */
    public static boolean clicked(int buttonID) {
        return inputSystemBackend.clicked0(buttonID);
    }

    /**
     * Test for a button release.
     * 
     * @param buttonID The mouse button id.
     * @return Whether it's been pressed.
     */
    public boolean released0(int buttonID) {
        return mouseUp.contains(buttonID);
    }

    /**
     * Was a mouse button released this frame?
     * 
     * @param buttonID Button id.
     * @return Whether it's released this frame or not.
     */
    public static boolean released(int buttonID) {
        return inputSystemBackend.released0(buttonID);
    }

    /**
     * Submit a key down request.
     * 
     * @param keyCode Input key code.
     */
    public void keyPressed(int keyCode) {
        keysDownBuffer.add(keyCode);

        if (!keysActive.contains(keyCode))
            keysActive.add(keyCode);
    }

    /**
     * Inform the input system a key has been released.
     * 
     * @param keyCode Code.
     */
    public void keyReleased(int keyCode) {
        keysUpBuffer.add(keyCode);
        keysActive.remove((Integer)keyCode); // Remove the object, not the index.
    }

    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    /**
     * Inform the input system of a mouse event.
     * 
     * @param mouseButton
     */
    public void mousePressed(int mouseButton) {
        mouseDownBuffer.add(mouseButton);

        if (!mouseActive.contains(mouseButton))
            mouseActive.add(mouseButton);
    }

    /**
     * A mouse button has been released.
     * 
     * @param mouseButton
     */
    public void mouseReleased(int mouseButton) {
        mouseUpBuffer.add(mouseButton);
        mouseActive.remove((Integer)mouseButton); // Remove the object, not the index.
    }
}
