package velocity.sprite.ui;

import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.sprite.Sprite;
import velocity.util.Point;

public abstract class UIRenderable extends Sprite {
    public UIRenderable(Point pos, float rot, String name) {
        super(pos, rot, name);
    }

    public abstract void renderUI(DrawInfo d, FrameBuffer fb);
}
