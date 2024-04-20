package velocity.renderer.erp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import velocity.Driver;
import velocity.GlobalAppConfig;
import velocity.InputSystem;
import velocity.renderer.EventHandler;
import velocity.sprite.Camera;
import velocity.util.Point;

/**
 * Internal ERP event handler. Handles key events, resize, and other generic render
 * pipeline events that are too abstract to handle elsewhere in Velocity.
 */
class ERPEventHandler extends ComponentAdapter implements ActionListener, EventHandler, 
                                                          KeyListener, MouseListener {
    /**
     * ERP window frame.
     */
    private JFrame f;

    /**
     * Driver code. Used for running the game loop when the game loop timer fires.
     */
    private Driver main;

    /**
     * The embedded render pipeline. Essential for forcing framebuffer regen.
     */
    private EmbeddedRenderPipeline erp;

    /**
     * Handle key and mouse events and forward them to the application in a portable
     * way.
     */
    private InputSystem inputSystem;

    /**
     * Create an event handler for this renderer.
     * 
     * @param f The frame to track.
     * @param main The player loop.
     * @param erp The render pipeline.
     */
    public ERPEventHandler(JFrame f, Driver main, EmbeddedRenderPipeline erp) {
        this.main = main;
        this.f = f;
        this.erp = erp;

        this.inputSystem = InputSystem.createInputSystem();
    }

    /**
     * Internal draw handler.
     */
    public void onTimerTick() {
        main.gameLoop();
        this.f.repaint();
        
        // Purge keypresses.
        inputSystem.clearKeyBuffers();
    }

    /**
     * Fired when the JFrame is resized. Sets the camera resolution (and probably
     * should fire LVSwapchain's regenerate event).
     * 
     * @param e Event fired.
     */
    public void componentResized(ComponentEvent e) {
        Dimension d = f.getContentPane().getSize();
        Camera.res = new Point((int)d.getWidth(), (int)d.getHeight());

        if (GlobalAppConfig.bcfg.EN_RENDERER_LOGS)
            System.out.println("[erp.event]: Window resized to " + Camera.res);

        this.erp.regenFrameBuffers();
    }

    /**
     * Unused.
     */
    public void actionPerformed(ActionEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}

    /**
     * Mouse Pressed event. Fires when a mouse button is pressed.
     * 
     * @param e Mouse Event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int code = e.getButton();
        inputSystem.mousePressed(code);
    }

    /**
     * Mouse Released event. Fires when a mouse button is released.
     * 
     * @param e Mouse Event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int code = e.getButton();
        inputSystem.mouseReleased(code);
    }

    /**
     * Key Pressed event. Fires when a key is pressed.
     * 
     * @param e Key Event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        inputSystem.keyPressed(code);
    }

    /**
     * Key released event. Fires when a key is released.
     * 
     * @param e Key Event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        inputSystem.keyReleased(code);
    }   
}
