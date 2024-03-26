package velocity.renderer;

import java.util.HashMap;

import velocity.util.Point;

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * Velocity's standard Lighting Engine. Allows simple and flexible light management
 * and illumination.
 */
public abstract class LightingEngine {
    /**
     * Internal light representation. Allows light lookups and operation.
     */
    protected HashMap<Long, InternalLightSource> lights;

    /**
     * Creates and initializes the basic lighting engine. Not to be used outside
     * of the defined renderer.
     */
    public LightingEngine() {
        this.lights = new HashMap<Long, InternalLightSource>();
    }



    /**
     * Internally create a light. Callback from {@code engine.lighting.PointLight}
     * on instantiation. Color info should be converted internally to 3 floats.
     * 
     * @param p Initial light location.
     * @param r Initial light radius.
     * @param intensity Initial light intensity.
     * @param c Initial light color (specified as int ranges 0-255)
     * @return Unique Light ID.
     */
    public abstract long newPointLight(Point p, float r, float intensity, Color c);

    /**
     * Internally create a light. Callback from {@code engine.lighting.SunLight}
     * on instantiation. Color info should be converted internally to 3 floats.
     * 
     * @param intensity Initial light intensity.
     * @param c Initial light color (specified as int ranges 0-255)
     * @return Unique Light ID.
     */
    public abstract long newSunLight(float intensity, Color c);

    /**
     * Gets a light object from the renderer uuid.
     * 
     * @param lightid Light Unique identifier.
     * @return LightSource reference.
     */
    public abstract InternalLightSource getLightSourceFromID(long lightid);

    /** WARNING! Eventually move this into LVCPU only. Other renderers don't 
     * need this function exposed. Will need to be updated eventually.
     * Allows the main shading illumination pass.
     * 
     * @param loc Input texture location in screen space.
     * @param in Input texture.
     * @return Shaded and illuminated texture.
     */
    public abstract BufferedImage illuminate(Point loc, BufferedImage in);



    /**
     * Add and register a light source for illumination.
     * 
     * @param light Light to be registered.
     * @return Light UID
     */
    public abstract long registerLightSource(InternalLightSource light);

    /**
     * Unregister and delete an existing light source.
     * 
     * @param lightid Light's unique identifier.
     */
    public abstract void deleteLightSource(long lightid);
}
