package com.rsc_games.velocity.lighting;

import com.rsc_games.velocity.VXRA;

import com.rsc_games.velocity.renderer.LightingEngine;
import com.rsc_games.velocity.util.Logger;
import com.rsc_games.velocity.util.Point;

/**
 * General abstract LightSource representation in Velocity. Hides the
 * internal details of the VXRA light sources.
 */
// TODO: Switch to a factory system and eliminate the InternalLightSource inheritance hierarchy.
// Remove the unique id system and unify the light sources and internal light sources under one
// system.
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
    @SuppressWarnings("deprecated")
    protected void finalize() throws Throwable {
        super.finalize();
        Logger.log("velocity.lighting", "Deallocated light found. Deleting.");
        if (!lePresent())
            return;

        this.le.deleteLightSource(this.lightid);
    }
}
