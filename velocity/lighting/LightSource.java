package velocity.lighting;

import velocity.VXRA;
import velocity.renderer.LightingEngine;
import velocity.util.Point;

public abstract class LightSource {
    protected LightingEngine le;
    protected long lightid;

    public LightSource() {
        this.le = VXRA.rp.le;
        this.lightid = 0L;
    }

    public abstract void setPos(Point pos);
    public abstract void setIntensity(float intensity);

    protected void finalize() {
        System.out.println("[velocity.lighting]: Deallocated light found. Deleting.");
        if (!lePresent()) {
            System.out.println("[velocity.lighting]: No Lighting Engine present. Skipping.");
            return;
        }

        this.le.deleteLightSource(this.lightid);
    }

    protected boolean lePresent() {
        return VXRA.rp.getFeatureSet().FEAT_lighting;
    }

    public void delete() {
        if (!lePresent()) return;

        this.le.deleteLightSource(lightid);
    }
}
