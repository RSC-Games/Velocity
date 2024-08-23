package velocity.renderer.erp;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.PointerInfo;

import javax.swing.JFrame;
import javax.swing.JPanel;

import velocity.renderer.DrawTimer;
import velocity.renderer.window.Window;
import velocity.renderer.window.WindowConfig;
import velocity.renderer.window.WindowOption;
import velocity.system.Images;
import velocity.util.Point;
import velocity.util.Warnings;
import velocity.Driver;

/**
 * An abstract representation of a window for the ERP. Allows limited control over the window presented
 * on the screen. Concrete implementation based on AWT.
 */
class ERPWindow extends JPanel implements Window {
    /**
     * The Embedded Render Pipeline.
     */
    EmbeddedRenderPipeline erp;

    /**
     * The frame presented on screen.
     */
    JFrame f;

    /**
     * The game loop code.
     */
    Driver driver;

    /**
     * The ERP event handler (for key/mouse events and window events).
     */
    ERPEventHandler erpEvent;

    /**
     * The frame redraw timer.
     */
    DrawTimer drawTimer;

    /**
     * Create the window context for the ERP to use.
     * 
     * @param cfg Window configuration.
     * @param erp The instantiating ERP.
     * @param m The main class.
     */
    @SuppressWarnings("deprecation")
    public ERPWindow(WindowConfig cfg, EmbeddedRenderPipeline erp, Driver m) {
        this.erp = erp;

        this.f = new JFrame(cfg.getTitle() + " <ERP on AWT (CPU SC)>");
        Point windowSize = cfg.getWindowResolution();

        // Set the desired window parameters.
        this.f.setSize(windowSize.x, windowSize.y);
        this.f.setMinimumSize(new Dimension(windowSize.x, windowSize.y));
        this.f.add(this); // Install the window listener.

        // Install the window event system.
        this.erpEvent = new ERPEventHandler(f, m, erp);
        this.f.addComponentListener(erpEvent);

        // Remaining frame init.
        this.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.f.setIconImage(Images.loadRawImage(cfg.getIconPath()));
        this.f.setAlwaysOnTop(cfg.getOption(WindowOption.HINT_ALWAYS_ON_TOP));
        this.f.setResizable(cfg.getOption(WindowOption.HINT_RESIZABLE));
        
        // Set fullscreen.
        if (cfg.getOption(WindowOption.HINT_FULLSCREEN))
            enterFullScreen();

        // Set up input handlers
        this.f.addMouseListener(erpEvent);
        this.f.addKeyListener(erpEvent);
    }

    /**
     * VXRA API method:
     * Return this window's current resolution. May change based on previously issued
     * resize events.
     * 
     * @return Window resolution.
     */
    @Override
    public Point getResolution() {
        Dimension d = this.f.getContentPane().getSize();
        return new Point((int)d.getWidth(), (int)d.getHeight());
    }

    /**
     * VXRA API method:
     * Returns this window's current position on screen. May change if the player moves
     * the window.
     * 
     * @return Current window position on screen.
     */
    @Override
    public Point getPosition() {
        java.awt.Point d = this.f.getContentPane().getComponent(0).getLocationOnScreen();
        return new Point((int)d.getX(), (int)d.getY());
    }

    /**
     * VXRA API Method.
     * Return the mouse pointer location on screen.
     * 
     * @return The mouse pointer location relative to the window origin.
     */
    @Override
    public Point getPointerLocation() {
        PointerInfo mInfo = MouseInfo.getPointerInfo();

        if (mInfo == null) {
            Warnings.warn("velocity", 
                "Got no mouse data! Cannot determine pointer location.");
            return Point.zero;
        }

        java.awt.Point screenMPos = mInfo.getLocation();
        Point windowMousePos = 
            new Point((int)screenMPos.getX(), (int)screenMPos.getY()).sub(getPosition());
        return windowMousePos;
    }

    /**
     * VXRA API method:
     * Set the window's visibility on screen. Initially is not visible.
     * 
     * @param state Show window (if {@code true}) or hide it (if {@code false}).
     */
    @Override
    public void setVisible(boolean state) {
        this.f.setVisible(state);
    }
    
    /**
     * Non-standard renderer method (used only by ERP). May be implemented by
     * other renderers if necessary.
     * 
     * @param msPerFrame milliseconds between each firing of the draw timer.
     */
    public void startEventTimer(int msPerFrame) {
        this.drawTimer = new DrawTimer(msPerFrame, this.erpEvent);
    }

    /**
     * Non-standard renderer method (used only by the ERP). May be implemented
     * by other renderers if necessary.
     * 
     * @return The initialized drawTimer.
     */
    public DrawTimer getTimer() {
        return this.drawTimer;
    }

    /**
     * When a paint call is finally issued, the currently drawn buffers will need
     * to be drawn on screen.. There are two buffers per frame as implemented in 
     * {@code EmbeddedRenderPipeline}: one for the scene and one for UI compositing. 
     * Output them both.
     * 
     * @param g Graphics object to draw to.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ERPFrameBuffer[] buffers = this.erp.getDrawBuffers();

        ERPFrameBuffer fb = buffers[0];
        ERPFrameBuffer uifb = buffers[1];

        fb.blitTo(g);
        uifb.blitTo(g);
        this.erp.clearRenderFlag();
    }

    /**
     * Put this window in fullscreen mode.
     */
    @Override
    public void enterFullScreen() {
        this.f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.f.setUndecorated(true);
    }

    /**
     * Bring this window out of fullscreen mode.
     */
    @Override
    public void exitFullScreen() {
        this.f.setExtendedState(JFrame.NORMAL);
        this.f.setUndecorated(false);
    }
}
