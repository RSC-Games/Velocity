package velocity.renderer.erp;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import velocity.renderer.DrawTimer;
import velocity.renderer.window.Window;
import velocity.renderer.window.WindowConfig;
import velocity.renderer.window.WindowOption;
import velocity.util.Point;
import velocity.Driver;
import velocity.Images;

class ERPWindow extends JPanel implements Window {
    EmbeddedRenderPipeline erp;
    JFrame f;
    Driver driver;
    ERPEventHandler erpEvent;
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
        if (cfg.getOption(WindowOption.HINT_FULLSCREEN)) {
            this.f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.f.setUndecorated(true);
        }

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
}
