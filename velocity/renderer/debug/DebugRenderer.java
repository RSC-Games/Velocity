package velocity.renderer.debug;

import javax.swing.JFrame;
import javax.swing.JPanel;

import velocity.*;
import velocity.renderer.DrawTimer;
import velocity.renderer.FrameBuffer;
import velocity.renderer.EventHandler;

import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;

// Mainly a special camera that allows easier debugging.
public class DebugRenderer extends JPanel implements EventHandler {
    private static Point DEBUG_FRAME_SIZE = new Point(400, 320);

    private JFrame df;
    private velocity.util.Point pos = new velocity.util.Point(0, 0);
    private velocity.util.Point offset;
    private FrameBuffer fb;
    public boolean run = true;
    private Font font = new Font("Serif", Font.PLAIN, 12);

    public DebugRenderer() {
        df = new JFrame("Debug Renderer");

        df.setSize(DEBUG_FRAME_SIZE.x, DEBUG_FRAME_SIZE.y);
        df.setMinimumSize(new Dimension(DEBUG_FRAME_SIZE.x, DEBUG_FRAME_SIZE.y));
        df.add(this);

        //df.setDefaultCloseOperation();
        df.setVisible(true);
        df.setAlwaysOnTop(false);
        df.addKeyListener(new DebugInput()); // Debug camera needs its own due to current limitations

        this.fb = new DebugFrameBuffer(DEBUG_FRAME_SIZE.x, DEBUG_FRAME_SIZE.y);
    }

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

    public void tick() {
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

    // TODO: Found memleaking bug ?
    public void draw() {
        if (Scene.currentScene == null) { return; }

        // Generate framebuffer.
        velocity.util.Point windowSize = getWindowSize();
        FrameBuffer lfb = new DebugFrameBuffer(windowSize.x, windowSize.y);
        
        if (Scene.currentScene == null) { return; }
        Scene.currentScene.DEBUG_render(lfb, pos, new float[] {1f, 1f});

        // Show mouse coordinates in the top area of the screen.
        Point p = MouseInfo.getPointerInfo().getLocation();
        velocity.util.Point mp = new velocity.util.Point((int)p.getX(), (int)p.getY()).sub(offset).add(pos);
        lfb.drawText(new velocity.util.Point(0, 10), "Mouse: " + mp, font, new Color(255, 255, 255));

        this.fb = lfb;
    }

    public void onTimerTick() {
        tick();
        draw();
        this.df.repaint();
    }

    public void paintComponent(Graphics g) {
        if (this.fb == null) return;
        ((DebugFrameBuffer)this.fb).blitTo(g);
    }

    private velocity.util.Point getWindowSize() {
        Dimension d = df.getContentPane().getSize();
        return new velocity.util.Point((int)d.getWidth(), (int)d.getHeight());
    }
}
