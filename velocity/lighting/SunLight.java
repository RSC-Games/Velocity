package velocity.lighting;

import java.awt.Color;

import velocity.renderer.InternalLightSource;
import velocity.util.Point;

/**
 * Global light source abstraction. Represents an overhead light generally.
 */
public class SunLight extends LightSource {
    /**
     * Create a new SunLight.
     * 
     * @param intensity Light intensity.
     */
    public SunLight(float intensity) {
        this(intensity, Color.white);
    }

    /**
     * Create a sunlight and specify its color.
     * 
     * @param intensity Light intensity.
     * @param color The color of the light.
     */
    public SunLight(float intensity, Color color) {
        super();
        if (!lePresent()) {
            System.out.println("[velocity.lighting]: No Lighting Engine present. Skipping.");
            return;
        }

        this.lightid = this.le.newSunLight(intensity, color);
    }

    /**
     * Set the light intensity.
     * 
     * @param intensity New light intensity.
     */
    public void setIntensity(float intensity) {
        if (!lePresent())
            return;

        this.getSource().setIntensity(intensity);
    }

    /**
     * Get the internal light source from the lighting engine.
     * 
     * @return The internal light source.
     */
    private InternalLightSource getSource() {
        return this.le.getLightSourceFromID(this.lightid);
    }
    
    /**
     * Stubbed function. Unsupported operation.
     */
    // TODO: Remnant of bad abstraction.
    public void setPos(Point p) {}
}
