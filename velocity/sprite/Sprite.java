package velocity.sprite;

import velocity.GlobalAppConfig;
import velocity.renderer.DrawInfo;
import velocity.renderer.FrameBuffer;
import velocity.util.MemTracerUtil;
import velocity.util.Point;
import velocity.Rect;

public class Sprite {
    public final Rect pos;
    public float rot;
    public final String name;
    public int sortOrder = 0;

    // Since no image is associated, no length & width should be necessary.
    public Sprite(Point pos, float rot, String name) {
        this.pos = new Rect(pos, 0, 0);
        this.rot = rot;
        this.name = name;

        /**
        if (AppConfig.LOG_MEMORY) {
            System.out.println("[MemTracer]: Creating new sprite of type " 
                + this.getClass().getSimpleName() + ". Ref count: " + ++spriteCount);
        }
        */
        if (GlobalAppConfig.bcfg.LOG_MEMORY)
            MemTracerUtil.trackSprite(this);
    }

    public void init() {}
    public void tick() {}
    public void delete() {}

    /**
     * Internal use only. Helper for debugging.
     */
    public void DEBUG_render(FrameBuffer fb, DrawInfo info) {}

    public void finalize() {
        if (GlobalAppConfig.bcfg.LOG_GC)
            System.out.println("[Scene.GC{Sprite}]: Sprite " + name + " GC'd!");

        if (GlobalAppConfig.bcfg.LOG_MEMORY)
            MemTracerUtil.removeTracking(this);
    }
}
