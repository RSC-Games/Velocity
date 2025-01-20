package velocity.sprite.ui;

import java.awt.Color;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.*;

/**
 * Sample FPS counter. Easily usable and flexible. Always shows the time between
 * this frame drawing and the last drawing.
 */
public class FPSCounter extends UIText {
    /**
     * The last observed counter value.
     */
    long counter = 0;

    /**
     * Create an FPS counter.
     * 
     * @param pos The screen position.
     * @param rot The rotation angle of the text.
     * @param name The name of the sprite.
     * @param c The text color.
     */
    public FPSCounter(Transform transform, String name, Color c) {
        super(transform, name, "Serif", c);
        this.transform.sortOrder = 1;
        this.counter = System.nanoTime();
        this.color = c;
    }

    /**
     * Draw during the UI compositing stage.
     * Counter values updated during this stage.
     * 
     * @param d Draw info.
     * @param fb Rendering framebuffer.
     */
    @Override
    public void renderUI(DrawInfo d, FrameBuffer fb) {    
        long cur = System.nanoTime();
        long delta = (cur - this.counter) / 1000000;

        Point coords = d.drawRect.getDrawLoc();
        fb.drawText(coords, "FPS: " + (1000 / (float)delta),
                    this.font, this.color);
        this.counter = cur;
    }
}
