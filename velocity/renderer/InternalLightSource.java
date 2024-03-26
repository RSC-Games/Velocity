package velocity.renderer;

import java.awt.Color;

import velocity.util.Point;

/**
 * Internal engine lighting representation. By default you must implement
 * at least xxSunLight and xxPointLight.
 */
public abstract class InternalLightSource {
    /**
     * Internal light intensity.
     */
    protected float intensity;

    /**
     * Internal light color.
     */
    protected Color c;

    /**
     * Renderer-specific unique light identifier.
     */
    protected long lightid;


    /**
     * Point light specific. Directly sets the location of the light.
     * Doesn't do anything on other light types.
     * 
     * @param pos New light position.
     */
    public abstract void setPos(Point pos);

    /**
     * Get the light unique identifier. Generally used for looking up the light
     * in the renderer itself.
     * 
     * @return Light unique identifer.
     */
    public final long getLightID() {
        return this.lightid;
    }

    /**
     * Set the light intensity.
     * 
     * @param intensity Light intensity.
     */
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    /**
     * Set the light radius.
     * 
     * @param radius Light radius.
     */
    public abstract void setRadius(float radius);

    /**
     * Get this light's intensity.
     * 
     * @return The light intensity.
     */
    public float getIntensity() {
        return this.intensity;
    }
}
