package velocity.lighting;

import java.awt.Color;

import velocity.renderer.InternalLightSource;
import velocity.util.Point;

/**
 * Point Light implementation. A point light has a source point and illumination 
 * rolls off in a circle around the center.
 */
public class PointLight extends LightSource {
    /**
     * Create a new point light.
     * 
     * @param center The center point of the light.
     * @param radius The radius of the light.
     */
    public PointLight(Point center, float radius) {
        this(center, radius, 1f);
    }

    /**
     * Create a new point light. Allows specifying the light intensity
     * as well.
     * 
     * @param center The center point of the light.
     * @param radius The radius of the light.
     * @param intensity The intensity of the light.
     */
    public PointLight(Point center, float radius, float intensity) {
        this(center, radius, intensity, Color.white);
    }

    /**
     * Create a new point light. Allows specifying the light intensity
     * as well and color of the light.
     * 
     * @param center The center point of the light.
     * @param radius The radius of the light.
     * @param intensity The intensity of the light.
     * @param color The light color.
     */
    public PointLight(Point center, float radius, float intensity, Color color) {
        super();
        if (!lePresent()) 
            return;

        this.lightid = this.le.newPointLight(center, radius, intensity, color);
    }

    /**
     * Set the position of a light.
     * 
     * @param pos New position.
     */
    @Override
    public void setPos(Point p) {
        if (!lePresent()) 
            return;

        this.getSource().setPos(p);
    }

    /**
     * Set the light intensity.
     * 
     * @param intensity New light intensity.
     */
    @Override
    public void setIntensity(float intensity) {
        if (!lePresent()) return;
        
        this.getSource().setIntensity(intensity);
    }

    /**
     * Set the light radius.
     * 
     * @param radius The new light radius.
     */
    public void setRadius(float radius) {
        if (!lePresent()) return;
        this.getSource().setRadius(radius);
    }

    /**
     * Get the internal light source. Used for getting the actual
     * abstracted light source.
     * 
     * @return The internal light source.
     */
    private InternalLightSource getSource() {
        return this.le.getLightSourceFromID(this.lightid);
    }
}
