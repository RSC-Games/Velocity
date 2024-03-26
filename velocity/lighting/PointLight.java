package velocity.lighting;

import java.awt.Color;

import velocity.renderer.InternalLightSource;
import velocity.util.Point;

public class PointLight extends LightSource {

    public PointLight(Point center, float radius) {
        this(center, radius, 1f);
    }

    public PointLight(Point center, float radius, float intensity) {
        this(center, radius, intensity, Color.white);
    }

    public PointLight(Point center, float radius, float intensity, Color color) {
        super();
        if (!lePresent()) {
            System.out.println("[velocity.lighting]: No Lighting Engine present. Skipping.");
            return;
        }

        this.lightid = this.le.newPointLight(center, radius, intensity, color);
    }

    public void setPos(Point p) {
        if (!lePresent()) {
            System.out.println("[velocity.lighting]: No Lighting Engine present. Skipping.");
            return;
        }

        this.getSource().setPos(p);
    }

    public void setIntensity(float intensity) {
        if (!lePresent()) return;
        
        this.getSource().setIntensity(intensity);
    }

    public void setRadius(float radius) {
        if (!lePresent()) return;
        this.getSource().setRadius(radius);
    }

    private InternalLightSource getSource() {
        return this.le.getLightSourceFromID(this.lightid);
    }
}
