package velocity.renderer.debug;

import java.awt.event.*;
import java.util.ArrayList;

// Useful for trapping keyboard and mouse inputs.
public class DebugInput implements KeyListener {
    private static ArrayList<Integer> keysDown = new ArrayList<Integer>();
    private static ArrayList<Integer> keysUp = new ArrayList<Integer>();
    private static ArrayList<Integer> keysActive = new ArrayList<Integer>();

    public void actionPerformed(ActionEvent e) {
        //super.actionPerformed(e);
        System.out.println("Received some kind of event. Idk what but here it is " + e);
    }

    // Called once per frame by the FrameRedrawListener.
    public static void clearKeyBuffers() {
        keysDown.clear();
        keysUp.clear();
    }

    // Meant to be called externally.
    public static boolean getKey(int keyCode) {
        return keysActive.contains(keyCode);
    }

    public static int getAxis(int pos, int neg) {
        return (getKey(pos) ? 1 : 0) - (getKey(neg) ? 1 : 0);
    }

    public static boolean getKeyUp(int keyCode) {
        return keysUp.contains(keyCode);
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysDown.add(keyCode);

        if (!keysActive.contains(keyCode))
            keysActive.add(keyCode);
    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysUp.add(keyCode);
        keysActive.remove((Integer)keyCode);
    }

    public void keyTyped(KeyEvent e) {
        // ignored.
    }
}
