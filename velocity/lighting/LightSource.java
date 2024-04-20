package velocity.lighting;

import velocity.VXRA;
import velocity.renderer.LightingEngine;
import velocity.util.Point;

/**
 * General abstract LightSource representation in Velocity. Hides the
 * internal details of the VXRA light sources.
 */
public abstract class LightSource {
    /**
     * Owning LightingEngine.
     */
    protected LightingEngine le;

    /**
     * The issued light ID from the renderer.
     */
    protected long lightid;

    /**
     * Base constructor. Not really used by any code.
     */
    public LightSource() {
        this.le = VXRA.rp.le;
        this.lightid = 0L;
    }

    /**
     * Set the position of a light. Not supported for SunLights. Why is this
     * even abstract anyway?
     * 
     * @param pos New position.
     */
    public abstract void setPos(Point pos);

    /**
     * Set the light intensity.
     * 
     * @param intensity New light intensity.
     */
    public abstract void setIntensity(float intensity);

    /**
     * Identify whether any lighting engine exists. If the render pipeline is not
     * lighting-capable, then no interaction should occur.
     * 
     * @return The presence of a lighting engine.
     */
    protected boolean lePresent() {
        return VXRA.rp.getFeatureSet().FEAT_lighting;
    }

    /**
     * Delete this light source.
     */
    public void delete() {
        if (!lePresent()) return;
        this.le.deleteLightSource(lightid);
    }

    /**
     * Automagically delete this light source after a scene destruction
     * or other event.
     */
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("[velocity.lighting]: Deallocated light found. Deleting.");
        if (!lePresent()) {
            System.out.println("[velocity.lighting]: No Lighting Engine present. Skipping.");
            return;
        }

        this.le.deleteLightSource(this.lightid);
    }
}
