package velocity.sprite;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.*;

public abstract class Renderable extends Sprite {
    public Renderable(Point pos, float rot, String name) {
        super(pos, rot, name);
    }

    public abstract void render(DrawInfo d, FrameBuffer g);
}
