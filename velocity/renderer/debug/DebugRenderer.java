package velocity.renderer.debug;

import javax.swing.JFrame;
import javax.swing.JPanel;

import velocity.*;
import velocity.renderer.DrawTimer;
import velocity.renderer.EventHandler;

import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;


/**
 * Mainly a special camera that allows easier debugging. Renders its own
 * frame and is locked to AWT. Badly written and yet not worth rewriting.
 */
public class DebugRenderer extends JPanel implements EventHandler {
    /**
     * The frame size for rendering.
     */
    private static velocity.util.Point DEBUG_FRAME_SIZE = new velocity.util.Point(400, 320);

    /**
     * The debug frame.
     */
    private JFrame df;

    /**
     * The current location on the screen.
     */
    private velocity.util.Point pos = new velocity.util.Point(0, 0);

    /**
     * Current window size. Allows dynamic window resizes and efficient allocation
     * of memory for framebuffers.
     */
    private velocity.util.Point windowSize = new velocity.util.Point(0, 0);

    /**
     * Window offset.
     */
    private velocity.util.Point offset;

    /**
     * Renderer framebuffer.
     */
    private DebugFrameBuffer fb;

    /**
     * Run the tick thread or disable it.
     */
    public boolean run = true;

    /**
     * Font object for text rendering.
     */
    private Font font = new Font("Serif", Font.PLAIN, 12);

    /**
     * Create the Debug Renderer object.
     */
    public DebugRenderer() {
        df = new JFrame("Debug Renderer (Velocity " + VelocityMain.VELOCITY_VER + ")");

        df.setSize(DEBUG_FRAME_SIZE.x, DEBUG_FRAME_SIZE.y);
        df.setMinimumSize(new Dimension(DEBUG_FRAME_SIZE.x, DEBUG_FRAME_SIZE.y));
        df.add(this);

        //df.setDefaultCloseOperation();
        df.setVisible(true);
        df.setAlwaysOnTop(false);
        df.addKeyListener(new DebugInput()); // Debug camera needs its own due to past limitations

        this.fb = new DebugFrameBuffer(DEBUG_FRAME_SIZE.x, DEBUG_FRAME_SIZE.y);
        this.windowSize = DEBUG_FRAME_SIZE;
    }

    /**
     * Initialize the debug renderer.
     */
    public void init() {
        DebugRenderer dr = this;
        new Thread() {
            public void run() {
                DrawTimer nt = new DrawTimer(16, dr);
                while (true) {
                    nt.tick();
                    if (!dr.run) return;
                }
            }
        }.start();
    }

    /**
     * Simulate the debug renderer.
     */
    void tick() {
        velocity.util.Point move = new velocity.util.Point(
            DebugInput.getAxis(KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT) * 10,
            -DebugInput.getAxis(KeyEvent.VK_UP, KeyEvent.VK_DOWN) * 10
        );
        pos = pos.add(move);
        
        // FIX (Crash on closed window)
        if (!df.getContentPane().getComponent(0).isShowing()) {
            this.run = false;
            return;
        }

        Point d = df.getContentPane().getComponent(0).getLocationOnScreen();
        offset = new velocity.util.Point((int)d.getX(), (int)d.getY());
    }

    /**
     * Draw the framebuffer on screen.
     */
    void draw() {
        if (Scene.currentScene == null) { return; }

        // Generate framebuffer if a window resize occurred.
        velocity.util.Point windowSize = getWindowSize();
        if (windowSize != this.windowSize)
            this.fb = new DebugFrameBuffer(windowSize.x, windowSize.y);
        
        if (Scene.currentScene == null) { return; }
        Scene.currentScene.DEBUG_render(fb, pos, new float[] {1f, 1f});

        // Show mouse coordinates in the top area of the screen.
        Point p = MouseInfo.getPointerInfo().getLocation();
        velocity.util.Point mp = new velocity.util.Point((int)p.getX(), (int)p.getY()).sub(offset).add(pos);
        fb.drawText(new velocity.util.Point(0, 10), "Mouse: " + mp, font, new Color(255, 255, 255));
    }

    /**
     * Execute the tick and draw code every timer update.
     */
    @Override
    public void onTimerTick() {
        tick();
        draw();
        this.df.repaint();
    }

    /**
     * Paint the component on screen. Already should be rendered.
     * 
     * @param g Graphics to draw to.
     */
    @Override
    public void paintComponent(Graphics g) {
        if (this.fb == null) return;
        this.fb.blitTo(g);
    }

    /**
     * Get the current window size.
     * 
     * @return The window size.
     */
    private velocity.util.Point getWindowSize() {
        Dimension d = df.getContentPane().getSize();
        return new velocity.util.Point((int)d.getWidth(), (int)d.getHeight());
    }
}
