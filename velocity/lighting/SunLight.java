package velocity.lighting;

import java.awt.Color;

import velocity.renderer.InternalLightSource;
import velocity.util.Point;

public class SunLight extends LightSource {

    public SunLight(float intensity) {
        this(intensity, Color.white);
    }

    public SunLight(float intensity, Color color) {
        super();
        if (!lePresent()) {
            System.out.println("[velocity.lighting]: No Lighting Engine present. Skipping.");
            return;
        }

        this.lightid = this.le.newSunLight(intensity, color);
    }

    public void setIntensity(float intensity) {
        if (!lePresent()) {
            System.out.println("[velocity.lighting]: No Lighting Engine present. Skipping.");
            return;
        }

        this.getSource().setIntensity(intensity);
    }

    private InternalLightSource getSource() {
        return this.le.getLightSourceFromID(this.lightid);
    }
    
    // Evidence of a required refactoring.
    public void setPos(Point p) {}
}
