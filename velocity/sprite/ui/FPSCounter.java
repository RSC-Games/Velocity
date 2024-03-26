package velocity.sprite.ui;

import java.awt.Color;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.*;

public class FPSCounter extends UIText {
    long counter = 0;

    public FPSCounter(Point pos, float rot, String name, Color c) {
        super(pos, rot, name, "Serif", c);
        this.sortOrder = 1;
        this.counter = System.nanoTime();
        this.color = c;
    }

    // FPS tracking works as is.
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
